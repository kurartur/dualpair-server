package lt.dualpair.server.service.socionics.test;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalTestEvaluatorTest {

    private LocalTestEvaluator localTestEvaluator;
    private SociotypeRepository sociotypeRepository;
    private EmbeddedDatabase db;

    @Before
    public void setUp() throws Exception {
        localTestEvaluator = new LocalTestEvaluator();
        sociotypeRepository = mock(SociotypeRepository.class);
        when(sociotypeRepository.findOne(1)).thenReturn(createSociotype(1, Sociotype.Code2.ESTJ));
        localTestEvaluator.setSociotypeRepository(sociotypeRepository);
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("db/sql/socionics/test/create-db.sql")
                .addScript("db/sql/socionics/test/insert-data.sql")
                .build();
        localTestEvaluator.setJdbcTemplate(new JdbcTemplate(db));
    }

    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

    @Test
    public void testEvaluate_doesNotExist() throws Exception {
        assertNull(localTestEvaluator.evaluate(createChoices("CHOICE1", "CHOICE2")));
    }

    @Test
    public void testEvaluate_exist() throws Exception {
        Sociotype sociotype = localTestEvaluator.evaluate(createChoices("CHOICE3", "CHOICE4"));
        assertEquals(new Integer(1), sociotype.getId());
        assertEquals(Sociotype.Code2.ESTJ, sociotype.getCode2());
    }

    private Sociotype createSociotype(Integer id, Sociotype.Code2 code2) {
        return new Sociotype.Builder().id(id).code2(code2).build();
    }

    private Map createChoices(String choice1, String choice2) {
        Map<String, String> choices = new HashMap<>();
        choices.put("1", choice1);
        choices.put("2", choice2);
        return choices;
    }
}