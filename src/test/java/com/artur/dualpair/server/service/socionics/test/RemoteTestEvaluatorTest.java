package com.artur.dualpair.server.service.socionics.test;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.socionics.test.Choice;
import com.artur.dualpair.server.domain.model.socionics.test.ChoicePair;
import com.artur.dualpair.server.infrastructure.persistence.repository.ChoicePairRepository;
import com.artur.dualpair.server.infrastructure.persistence.repository.ChoiceRepository;
import com.artur.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RemoteTestEvaluatorTest {

    private RemoteTestEvaluator remoteTestEvaluator;
    private RemoteTestEvaluator.EvaluateRequest evaluateRequest;
    private ChoiceRepository choiceRepository = mock(ChoiceRepository.class);
    private ChoicePairRepository choicePairRepository = mock(ChoicePairRepository.class);
    private SociotypeRepository sociotypeRepository = mock(SociotypeRepository.class);

    @Before
    public void setUp() throws Exception {
        evaluateRequest = mock(RemoteTestEvaluator.EvaluateRequest.class);
        remoteTestEvaluator = new RemoteTestEvaluator() {
            @Override
            protected RemoteTestEvaluator.EvaluateRequest createRequest() {
                return evaluateRequest;
            }
        };
        when(choiceRepository.findByCode("CHOICE1")).thenReturn(createChoice(1, "CHOICE1", "10"));
        when(choiceRepository.findByCode("CHOICE2")).thenReturn(createChoice(2, "CHOICE2", "1"));
        when(choiceRepository.findByCode("CHOICE3")).thenReturn(createChoice(3, "CHOICE3", "10"));
        when(choiceRepository.findByCode("CHOICE4")).thenReturn(createChoice(4, "CHOICE4", "1"));
        when(choicePairRepository.findOne(1)).thenReturn(createChoicePair(1, "e1"));
        when(choicePairRepository.findOne(2)).thenReturn(createChoicePair(2, "e2"));
        when(sociotypeRepository.findByCode1(Sociotype.Code1.LSE)).thenReturn(createSociotype(1, Sociotype.Code1.LSE));
        remoteTestEvaluator.setChoiceRepository(choiceRepository);
        remoteTestEvaluator.setChoicePairRepository(choicePairRepository);
        remoteTestEvaluator.setSociotypeRepository(sociotypeRepository);
    }

    @Test
    public void testEvaluate() throws Exception {
        Map<String, String> choices = createChoices("CHOICE1", "CHOICE4");
        when(evaluateRequest.post("w1=on&w2=on&w4=on&w3=on&e1=10&e2=1&tip=cheb")).thenReturn("<td valign=top><p class=zz>Ваш социотип: <a href=http://socionika.info/tip/lse.html><u>логико-сенсорный экстраверт - \"Штирлиц\"</u></a></p><p><b>Социотип Вашего <a href=http://socionika.info/tip/dual.html><u>дуала</u></a>: <a href=http://socionika.info/tip/eii.html><u>этико-интуитивный интроверт - \"Достоевский\"</u></a></b></p><p><font color=red>");
        Sociotype sociotype = remoteTestEvaluator.evaluate(choices);
        verify(evaluateRequest, times(1)).post("w1=on&w2=on&w4=on&w3=on&e1=10&e2=1&tip=cheb");
        assertEquals(new Integer(1), sociotype.getId());
        assertEquals(Sociotype.Code1.LSE, sociotype.getCode1());
    }

    @Test
    public void testEvaluate_noMatchInResponse() throws Exception {
        when(evaluateRequest.post(any(String.class))).thenReturn("<td valign=top><p class=zz>Ва социотип: <a href=http://socionika.info/tip/lse.html><u>логико-сенсорный экстраверт - \"Штирлиц\"</u></a></p><p><b>Социотип Вашего <a href=http://socionika.info/tip/dual.html><u>дуала</u></a>: <a href=http://socionika.info/tip/eii.html><u>этико-интуитивный интроверт - \"Достоевский\"</u></a></b></p><p><font color=red>");
        try {
            remoteTestEvaluator.evaluate(new HashMap<>());
            fail();
        } catch (SocionicsTestException ste) {
            assertEquals(SocionicsTestException.codeNotFoundInResponse, ste.getMessage());
        }
    }

    @Test
    public void testEvaluate_sociotypeNotFound() throws Exception {
        when(evaluateRequest.post(any(String.class))).thenReturn("<td valign=top><p class=zz>Ваш социотип: <a href=http://socionika.info/tip/eii.html><u>логико-сенсорный экстраверт - \"Штирлиц\"</u></a></p><p><b>Социотип Вашего <a href=http://socionika.info/tip/dual.html><u>дуала</u></a>: <a href=http://socionika.info/tip/eii.html><u>этико-интуитивный интроверт - \"Достоевский\"</u></a></b></p><p><font color=red>");
        try {
            remoteTestEvaluator.evaluate(new HashMap<>());
            fail();
        } catch (SocionicsTestException ste) {
            assertEquals(SocionicsTestException.sociotypeNotFound, ste.getMessage());
        }
    }

    @Test
    public void testEvalute_requestError() throws Exception {
        when(evaluateRequest.post(any(String.class))).thenThrow(new SocionicsTestException(SocionicsTestException.evaluateRequestError));
        try {
            remoteTestEvaluator.evaluate(new HashMap<>());
            fail();
        } catch (SocionicsTestException ste) {
            assertEquals(SocionicsTestException.evaluateRequestError, ste.getMessage());
        }
    }

    //@Test
    public void testEvaluateRequest() throws Exception {
        RemoteTestEvaluator.EvaluateRequest evaluateRequest = new RemoteTestEvaluator.EvaluateRequest();
        String response = evaluateRequest.post("w1=on&w2=on&w4=on&w3=on&r1=10&r2=10&r3=10&r4=10&r5=10&r6=10&r7=10&l1=10&l2=10&l3=10&l4=10&l5=10&l6=10&l7=10&s1=10&s2=10&s3=10&s4=10&s5=10&s6=10&s7=10&e1=10&e2=10&e3=10&e4=10&e5=10&e6=10&e7=10&tip=cheb");
        System.out.println(response);
    }

    private Map<String, String> createChoices(String choice1, String choice2) {
        Map<String, String> choices = new LinkedHashMap<>();
        choices.put("1", choice1);
        choices.put("2", choice2);
        return choices;
    }

    private Choice createChoice(Integer id, String code, String remoteValue) {
        return new Choice.Builder().id(id).code(code).remoteValue(remoteValue).build();
    }

    private ChoicePair createChoicePair(Integer id, String remoteId) {
        return new ChoicePair.Builder().id(id).remoteId(remoteId).build();
    }

    private Sociotype createSociotype(Integer id, Sociotype.Code1 code1) {
        return new Sociotype.Builder().id(id).code1(code1).build();
    }

}