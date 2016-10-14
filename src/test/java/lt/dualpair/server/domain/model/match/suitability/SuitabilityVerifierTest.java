package lt.dualpair.server.domain.model.match.suitability;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class SuitabilityVerifierTest {

    private SuitabilityVerifier checker;
    private AgeVerifier ageVerifier = mock(AgeVerifier.class);
    private GenderVerifier genderVerifier = mock(GenderVerifier.class);

    @Test
    public void testCheck() throws Exception {
        new SuitabilityVerifier(ageVerifier, genderVerifier);
    }

}