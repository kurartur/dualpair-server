package com.artur.dualpair.server.service.socionics.test;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LocalTestEvaluator implements TestEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(LocalTestEvaluator.class.getName());

    private SociotypeRepository sociotypeRepository;
    private JdbcTemplate jdbcTemplate;

    @Override
    public Sociotype evaluate(Map<String, String> choices) throws SocionicsTestException {
        try {
            Integer sociotypeId = getSociotypeId(choices);
            return sociotypeRepository.findOne(sociotypeId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            logger.error("Error while retrieving sociotype id", e);
            return null;
        }
    }

    private Integer getSociotypeId(Map<String, String> choices) {
        StringBuilder queryBuilder = new StringBuilder("" +
                "select tc.sociotype_id from test_combinations tc " +
                "inner join test_combinations_choices_view tccv on tccv.combination_id=tc.id " +
                "where ");
        Object[] parameters = new Object[choices.size()];
        int i = 0;
        String prefix = "";
        for (Map.Entry<String, String> choice : choices.entrySet()) {
            queryBuilder.append(prefix).append("pair").append(choice.getKey()).append("=?");
            prefix = " and ";
            parameters[i++] = choice.getValue();
        }
        return jdbcTemplate.queryForObject(queryBuilder.toString(), parameters, Integer.class);
    }

    @Autowired
    public void setSociotypeRepository(SociotypeRepository sociotypeRepository) {
        this.sociotypeRepository = sociotypeRepository;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
