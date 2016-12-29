package lt.dualpair.server.service.socionics.test;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.socionics.test.Choice;
import lt.dualpair.server.domain.model.socionics.test.ChoicePair;
import lt.dualpair.server.domain.model.socionics.test.Combination;
import lt.dualpair.server.infrastructure.persistence.repository.ChoicePairRepository;
import lt.dualpair.server.infrastructure.persistence.repository.CombinationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SocionicsTestServiceTest {

    private SocionicsTestService socionicsTestService;
    private CombinationRepository combinationRepository;
    private RemoteTestEvaluator remoteTestEvaluator;
    private LocalTestEvaluator localTestEvaluator;
    private ChoicePairRepository choicePairRepository = mock(ChoicePairRepository.class);

    @Before
    public void setUp() throws Exception {
        socionicsTestService = new SocionicsTestService();
        combinationRepository = mock(CombinationRepository.class);
        socionicsTestService.setCombinationRepository(combinationRepository);
        remoteTestEvaluator = mock(RemoteTestEvaluator.class);
        socionicsTestService.setRemoteTestEvaluator(remoteTestEvaluator);
        localTestEvaluator = mock(LocalTestEvaluator.class);
        socionicsTestService.setLocalTestEvaluator(localTestEvaluator);
        socionicsTestService.setChoicePairRepository(choicePairRepository);
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
    public void testEvaluate_whenLocalDoesNotExist_isEvaluatedRemotelyAndSaved() throws Exception {
        Map choices = createChoices();
        when(localTestEvaluator.evaluate(choices)).thenReturn(null);
        Sociotype resultSociotype = createSociotype(1, Sociotype.Code2.ESTJ);
        when(remoteTestEvaluator.evaluate(choices)).thenReturn(resultSociotype);

        Sociotype sociotype = socionicsTestService.evaluate(choices);
        assertEquals(new Integer(1), sociotype.getId());
        assertEquals(Sociotype.Code2.ESTJ, sociotype.getCode2());

        verifyCombinationSave();
    }

    @Test
    public void testEvaluate_whenLocalThrowsException_isEvaluatedRemotelyAndSaved() throws Exception {
        Map choices = createChoices();
        when(localTestEvaluator.evaluate(choices)).thenThrow(new RuntimeException("Error"));
        Sociotype resultSociotype = createSociotype(1, Sociotype.Code2.ESTJ);
        when(remoteTestEvaluator.evaluate(choices)).thenReturn(resultSociotype);

        Sociotype sociotype = socionicsTestService.evaluate(choices);
        assertEquals(new Integer(1), sociotype.getId());
        assertEquals(Sociotype.Code2.ESTJ, sociotype.getCode2());

        verifyCombinationSave();
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
    public void testEvaluate_whenUnableToSaveCombination_resultStillReturned() throws Exception {
        Map choices = createChoices();
        when(localTestEvaluator.evaluate(choices)).thenReturn(null);
        Sociotype resultSociotype = createSociotype(1, Sociotype.Code2.ESTJ);
        when(remoteTestEvaluator.evaluate(choices)).thenReturn(resultSociotype);
        doThrow(new RuntimeException("Error")).when(combinationRepository).save(any(Combination.class));
        Sociotype sociotype = socionicsTestService.evaluate(choices);
        assertEquals(new Integer(1), sociotype.getId());
        assertEquals(Sociotype.Code2.ESTJ, sociotype.getCode2());

        verifyCombinationSave();
    }

    private void verifyCombinationSave() {
        ArgumentCaptor<Combination> combinationCaptor = ArgumentCaptor.forClass(Combination.class);
        verify(combinationRepository, times(1)).save(combinationCaptor.capture());

        Combination combination = combinationCaptor.getValue();
        assertEquals(28, combination.getCombinationChoices().size());
    }

    private Map createChoices() {
        Map<String, String> choices = new HashMap<>();
        for (int i = 0; i < 28; i++) {
            choices.put(String.valueOf(i), String.valueOf(i));
            when(choicePairRepository.findOne(i)).thenAnswer(new ChoicePairAnswer());
        }
        return choices;
    }

    private Sociotype createSociotype(Integer id, Sociotype.Code2 code2) {
        return new Sociotype.Builder().id(id).code2(code2).build();
    }

    private class ChoicePairAnswer implements Answer<Optional> {

        @Override
        public Optional answer(InvocationOnMock invocation) throws Throwable {
            ChoicePair choicePair = new ChoicePair.Builder()
                    .choice1(new Choice.Builder().code(Integer.toString((Integer)invocation.getArguments()[0])).build())
                    .choice2(new Choice.Builder().code(Integer.toString((Integer)invocation.getArguments()[0])).build())
                    .build();
            return Optional.of(choicePair);
        }

    }
}