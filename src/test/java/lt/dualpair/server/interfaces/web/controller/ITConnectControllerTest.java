package lt.dualpair.server.interfaces.web.controller;

import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Test;
import org.mockito.internal.matchers.Contains;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ITConnectControllerTest extends BaseRestControllerTest {

    @Test
    public void testAllowed() throws Exception {
        mockMvc.perform(get("/connect/vkontakte").with(bearerToken(1L)))
                .andExpect(status().isOk());
    }

    @Test
    public void testForbiddenWithoutBearer() throws Exception {
        mockMvc.perform(get("/connect/vkontakte"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(new Contains("Full authentication is required")));
    }
}
