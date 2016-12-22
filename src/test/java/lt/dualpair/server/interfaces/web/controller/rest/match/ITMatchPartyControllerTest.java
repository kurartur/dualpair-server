package lt.dualpair.server.interfaces.web.controller.rest.match;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ITMatchPartyControllerTest extends BaseRestControllerTest {

    @Test
    public void testResponse_unauthorized() throws Exception {
        mockMvc.perform(post("/api/party/1/response"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_invalidUser() throws Exception {
        mockMvc.perform(put("/api/party/1/response").with(bearerToken(3L)).content("YES"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_notFound() throws Exception {
        mockMvc.perform(put("/api/party/100/response").with(bearerToken(1L)).content("YES"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_undefinedToYes() throws Exception {
        mockMvc.perform(put("/api/party/1/response").with(bearerToken(1L)).content("YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap("select response from match_parties where user_id=1");
        assertEquals("Y", rs.get("response"));
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_undefinedToNo() throws Exception {
        mockMvc.perform(put("/api/party/1/response").with(bearerToken(1L)).content("NO"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap("select response from match_parties where user_id=1");
        assertEquals("N", rs.get("response"));
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_undefinedToYes_mutual() throws Exception {
        mockMvc.perform(put("/api/party/1/response").with(bearerToken(1L)).content("YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap("select response from match_parties where user_id=1");
        assertEquals("Y", rs.get("response"));
        assertTrue(StringUtils.isEmpty(rs.get("mutual_time")));
        mockMvc.perform(put("/api/party/2/response").with(bearerToken(2L)).content("YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        rs = jdbcTemplate.queryForMap("select response from match_parties where user_id=2");
        assertEquals("Y", rs.get("response"));
        rs = jdbcTemplate.queryForMap("select mutual_time from matches where id=1");
        assertFalse(StringUtils.isEmpty(rs.get("mutual_time")));
    }

}
