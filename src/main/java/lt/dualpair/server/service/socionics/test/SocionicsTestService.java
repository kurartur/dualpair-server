package lt.dualpair.server.service.socionics.test;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.socionics.test.Choice;
import lt.dualpair.server.domain.model.socionics.test.ChoicePair;
import lt.dualpair.server.domain.model.socionics.test.Combination;
import lt.dualpair.server.domain.model.socionics.test.CombinationChoice;
import lt.dualpair.server.infrastructure.persistence.repository.ChoicePairRepository;
import lt.dualpair.server.infrastructure.persistence.repository.CombinationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SocionicsTestService {

    private static final Logger logger = LoggerFactory.getLogger(SocionicsTestService.class.getName());

    private static final int NUMBER_OF_PAIRS = 28;

    private RemoteTestEvaluator remoteTestEvaluator;
    private LocalTestEvaluator localTestEvaluator;
    private CombinationRepository combinationRepository;
    private ChoicePairRepository choicePairRepository;

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
            combinationRepository.save(buildCombination(choices, sociotype));
        } catch (Exception e) {
            logger.error("Unable to save combination ", e);
        }
        return sociotype;
    }

    private Combination buildCombination(Map<String, String> choices, Sociotype sociotype) throws SocionicsTestException {
        Set<CombinationChoice> combinationChoices = new HashSet<>();
        Combination combination = new Combination(combinationChoices, sociotype);
        for(Map.Entry<String, String> entry : choices.entrySet()) {
            String pairId = entry.getKey();
            String choiceCode = entry.getValue();
            ChoicePair choicePair = choicePairRepository.findOne(Integer.valueOf(pairId))
              .orElseThrow(() -> new RuntimeException("Choice pair " + pairId + " not found"));
            Choice choice;
            if (choicePair.getChoice1().getCode().equals(choiceCode)) {
                choice = choicePair.getChoice1();
            } else if (choicePair.getChoice2().getCode().equals(choiceCode)) {
                choice = choicePair.getChoice2();
            } else {
                throw new SocionicsTestException("Invalid choice " + choiceCode + " for choice pair " + pairId);
            }
            combinationChoices.add(new CombinationChoice(combination, choicePair, choice));
        }
        Assert.isTrue(combinationChoices.size() == NUMBER_OF_PAIRS);
        return combination;
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

    @Autowired
    public void setChoicePairRepository(ChoicePairRepository choicePairRepository) {
        this.choicePairRepository = choicePairRepository;
    }
}
