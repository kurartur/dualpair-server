package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.interfaces.dto.SociotypeDTO;
import com.artur.dualpair.server.service.socionics.test.SocionicsTestService;
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

    @Before
    public void setUp() throws Exception {
        socionicsController.setSocionicsTestService(socionicsTestService);
    }

    @Test
    public void testEvaluateTest() throws Exception {
        Map<String, String> choices = new HashMap<>();
        Sociotype sociotype = new Sociotype.Builder().code1(Sociotype.Code1.EII).build();
        when(socionicsTestService.evaluate(choices)).thenReturn(sociotype);
        ResponseEntity response = socionicsController.evaluateTest(choices);
        SociotypeDTO dto = (SociotypeDTO)response.getBody();
        assertEquals("EII", dto.getCode1());
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