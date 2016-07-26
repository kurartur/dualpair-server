package com.artur.dualpair.server.service.socionics.test;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.infrastructure.persistence.repository.CombinationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
public class SocionicsTestService {

    private static final Logger logger = LoggerFactory.getLogger(SocionicsTestService.class.getName());

    private static final int NUMBER_OF_PAIRS = 28;

    private RemoteTestEvaluator remoteTestEvaluator;
    private LocalTestEvaluator localTestEvaluator;
    private CombinationRepository combinationRepository;

    @Transactional
    public Sociotype evaluate(Map<String, String> choices) throws SocionicsTestException {
        if (choices.size() != NUMBER_OF_PAIRS) {
            throw new IllegalArgumentException(NUMBER_OF_PAIRS + " choices expected");
        }
        Sociotype sociotype;
        try {
            sociotype = localTestEvaluator.evaluate(choices);
        } catch (Exception e) {
            sociotype = evaluateAndSaveRemotely(choices);
        }
        if (sociotype == null) {
            sociotype = evaluateAndSaveRemotely(choices);
        }
        return sociotype;
    }

    private Sociotype evaluateAndSaveRemotely(Map<String, String> choices) throws SocionicsTestException {
        Sociotype sociotype = remoteTestEvaluator.evaluate(choices);
        try {
            combinationRepository.saveCombination(choices, sociotype);
        } catch (Exception e) {
            logger.error("Unable to save combination ", e);
        }
        return sociotype;
    }

    @Autowired
    public void setRemoteTestEvaluator(RemoteTestEvaluator remoteTestEvaluator) {
        this.remoteTestEvaluator = remoteTestEvaluator;
    }

    @Autowired
    public void setLocalTestEvaluator(LocalTestEvaluator localTestEvaluator) {
        this.localTestEvaluator = localTestEvaluator;
    }

    @Autowired
    public void setCombinationRepository(CombinationRepository combinationRepository) {
        this.combinationRepository = combinationRepository;
    }
}
