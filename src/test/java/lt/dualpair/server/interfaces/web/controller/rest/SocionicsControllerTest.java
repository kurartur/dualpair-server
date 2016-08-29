package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResource;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResourceAssembler;
import lt.dualpair.server.service.socionics.test.SocionicsTestService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SocionicsControllerTest {

    private SocionicsController socionicsController = new SocionicsController();
    private SocionicsTestService socionicsTestService = mock(SocionicsTestService.class);
    private SociotypeResourceAssembler sociotypeResourceAssembler = mock(SociotypeResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        socionicsController.setSocionicsTestService(socionicsTestService);
        socionicsController.setSociotypeResourceAssembler(sociotypeResourceAssembler);
    }

    @Test
    public void testEvaluateTest() throws Exception {
        Map<String, String> choices = new HashMap<>();
        Sociotype sociotype = new Sociotype.Builder().code1(Sociotype.Code1.EII).build();
        when(socionicsTestService.evaluate(choices)).thenReturn(sociotype);
        SociotypeResource sociotypeResource = new SociotypeResource();
        when(sociotypeResourceAssembler.toResource(sociotype)).thenReturn(sociotypeResource);
        ResponseEntity response = socionicsController.evaluateTest(choices);
        assertEquals(sociotypeResource, response.getBody());
    }

    @Test
    public void testEvaluateTest_exception() throws Exception {
        doThrow(new IllegalArgumentException("Error")).when(socionicsTestService).evaluate(any(Map.class));
        try {
            socionicsController.evaluateTest(new HashMap<>());
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Error", iae.getMessage());
        }
    }
}