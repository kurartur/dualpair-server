package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import lt.dualpair.server.interfaces.web.controller.rest.ErrorResponse;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup("reportTest.xml")
public class ITReportControllerTest extends BaseRestControllerTest {

    @Test
    public void report_unauthorized() throws Exception {
        mockMvc.perform(post("/api/report"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void report() throws Exception {
        mockMvc.perform(post("/api/report").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON).content("{\"user_id\":2}"))
                .andExpect(status().isCreated());
        flushPersistenceContext();
        Map<String, Object> result = jdbcTemplate.queryForMap("select * from user_reports where user_id=2");
        assertEquals(1L, result.get("reported_by"));
        assertEquals(2L, result.get("user_id"));
    }

    @Test
    public void report_whenUserDoesntExist_403() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/report").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON).content("{\"user_id\":3}"))
                .andExpect(status().isBadRequest()).andReturn();
        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        ErrorResponse response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals("User not found", response.getMessage());
    }

    @Test
    public void report_whenAlreadyReported_409() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/report").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON).content("{\"user_id\":4}"))
                .andExpect(status().isConflict()).andReturn();
        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        ErrorResponse response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals("User already reported", response.getMessage());
    }

    @Test
    public void report_whenReportLimitReached_409() throws Exception {
        jdbcTemplate.execute("update user_reports set report_date=now() where reported_by=2");
        MvcResult result = mockMvc.perform(post("/api/report").with(bearerToken(2L)).contentType(MediaType.APPLICATION_JSON).content("{\"user_id\":8}"))
                .andExpect(status().isConflict()).andReturn();
        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        ErrorResponse response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals("Report limit is reached", response.getMessage());
    }
}