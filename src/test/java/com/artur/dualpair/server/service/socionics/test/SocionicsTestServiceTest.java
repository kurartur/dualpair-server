package com.artur.dualpair.server.service.socionics.test;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.persistence.repository.CombinationRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SocionicsTestServiceTest {

    private SocionicsTestService socionicsTestService;
    private CombinationRepository combinationRepository;
    private RemoteTestEvaluator remoteTestEvaluator;
    private LocalTestEvaluator localTestEvaluator;

    @Before
    public void setUp() throws Exception {
        socionicsTestService = new SocionicsTestService();
        combinationRepository = mock(CombinationRepository.class);
        socionicsTestService.setCombinationRepository(combinationRepository);
        remoteTestEvaluator = mock(RemoteTestEvaluator.class);
        socionicsTestService.setRemoteTestEvaluator(remoteTestEvaluator);
        localTestEvaluator = mock(LocalTestEvaluator.class);
        socionicsTestService.setLocalTestEvaluator(localTestEvaluator);
    }

    @Test
    public void testEvaluate_wrongNumberOfChoices() throws Exception {
        Map<String, String> choices = new HashMap<>();
        try {
            socionicsTestService.evaluate(choices);
            fail();
        } catch(IllegalArgumentException iae) {
            assertEquals("28 choices expected", iae.getMessage());
        }
    }

    @Test
    public void testEvaluate_localExists() throws Exception {
        Map choices = createChoices();
        when(localTestEvaluator.evaluate(choices)).thenReturn(createSociotype(2, Sociotype.Code2.ENTJ));
        Sociotype sociotype = socionicsTestService.evaluate(choices);
        assertEquals(new Integer(2), sociotype.getId());
        assertEquals(Sociotype.Code2.ENTJ, sociotype.getCode2());
    }

    @Test
    public void testEvaluate_localDoesNotExist() throws Exception {
        Map choices = createChoices();
        when(localTestEvaluator.evaluate(choices)).thenReturn(null);
        Sociotype resultSociotype = createSociotype(1, Sociotype.Code2.ESTJ);
        when(remoteTestEvaluator.evaluate(choices)).thenReturn(resultSociotype);
        Sociotype sociotype = socionicsTestService.evaluate(choices);
        assertEquals(new Integer(1), sociotype.getId());
        assertEquals(Sociotype.Code2.ESTJ, sociotype.getCode2());
        verify(combinationRepository, times(1)).saveCombination(choices, resultSociotype);
    }

    @Test
    public void testEvaluate_localException() throws Exception {
        Map choices = createChoices();
        when(localTestEvaluator.evaluate(choices)).thenThrow(new RuntimeException("Error"));
        Sociotype resultSociotype = createSociotype(1, Sociotype.Code2.ESTJ);
        when(remoteTestEvaluator.evaluate(choices)).thenReturn(resultSociotype);
        Sociotype sociotype = socionicsTestService.evaluate(choices);
        assertEquals(new Integer(1), sociotype.getId());
        assertEquals(Sociotype.Code2.ESTJ, sociotype.getCode2());
        verify(combinationRepository, times(1)).saveCombination(choices, resultSociotype);
    }

    @Test
    public void testEvaluate_remoteException() throws Exception {
        Map choices = createChoices();
        when(localTestEvaluator.evaluate(choices)).thenReturn(null);
        when(remoteTestEvaluator.evaluate(choices)).thenThrow(new SocionicsTestException(SocionicsTestException.unableToEvaluateRemotely));
        try {
            socionicsTestService.evaluate(choices);
            fail();
        } catch (SocionicsTestException ste) {
            assertEquals(SocionicsTestException.unableToEvaluateRemotely, ste.getMessage());
        }
    }

    @Test
    public void testEvaluate_saveException() throws Exception {
        Map choices = createChoices();
        when(localTestEvaluator.evaluate(choices)).thenReturn(null);
        Sociotype resultSociotype = createSociotype(1, Sociotype.Code2.ESTJ);
        when(remoteTestEvaluator.evaluate(choices)).thenReturn(resultSociotype);
        doThrow(new RuntimeException("Error")).when(combinationRepository).saveCombination(choices, resultSociotype);
        Sociotype sociotype = socionicsTestService.evaluate(choices);
        assertEquals(new Integer(1), sociotype.getId());
        assertEquals(Sociotype.Code2.ESTJ, sociotype.getCode2());
        verify(combinationRepository, times(1)).saveCombination(choices, resultSociotype);
    }

    private Map createChoices() {
        Map<String, String> choices = new HashMap<>();
        for (int i = 0; i < 28; i++) {
            choices.put(String.valueOf(i), String.valueOf(i));
        }
        return choices;
    }

    private Sociotype createSociotype(Integer id, Sociotype.Code2 code2) {
        return new Sociotype.Builder().id(id).code2(code2).build();
    }
}