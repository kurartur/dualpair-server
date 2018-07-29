package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.user.UserResponseResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Test;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ITUserResponseControllerTest extends BaseRestControllerTest {

    @Test
    public void testRespond_unauthorized() throws Exception {
        mockMvc.perform(put("/api/user/1/responses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "userResponseTest.xml")
    public void testRespond_invalidUser() throws Exception {
        mockMvc.perform(
                put("/api/user/1/responses")
                    .with(bearerToken(3L))
                    .param("toUserId", "1")
                    .param("response", "YES"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "userResponseTest.xml")
    public void testRespond_userDoesntExist() throws Exception {
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "100")
                .param("response", "YES"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DatabaseSetup(value = "userResponseTest.xml")
    public void testRespond_yes() throws Exception {
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "2")
                .param("response", "YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap("select to_user_id, response from user_responses where user_id=1");
        assertEquals("Y", rs.get("response"));
        assertEquals(2L, rs.get("to_user_id"));
    }

    @Test
    @DatabaseSetup(value = "userResponseTest.xml")
    public void testRespond_no() throws Exception {
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "2")
                .param("response", "NO"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap("select to_user_id, response from user_responses where user_id=1");
        assertEquals("N", rs.get("response"));
        assertEquals(2L, rs.get("to_user_id"));
    }

    @Test
    @DatabaseSetup(value = "userResponseTest.xml")
    public void testRespond_yes_mutual() throws Exception {
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "2")
                .param("response", "YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap("select response from user_responses where user_id=1");
        assertEquals("Y", rs.get("response"));
        List list = jdbcTemplate.queryForList("select * from match_parties where user_id=1");
        assertTrue(list.isEmpty());
        mockMvc.perform(put("/api/user/2/responses").with(bearerToken(2L))
                .param("toUserId", "1")
                .param("response", "YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        rs = jdbcTemplate.queryForMap("select response from user_responses where user_id=2");
        assertEquals("Y", rs.get("response"));
        list = jdbcTemplate.queryForList("select * from match_parties where user_id=1 or user_id=2");
        assertEquals(2, list.size());
    }

    @Test
    @DatabaseSetup(value = "userResponseTest.xml")
    public void testRespond_responseAlreadyExists() throws Exception {
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "2")
                .param("response", "YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "2")
                .param("response", "NO"))
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup(value = "userResponseTest.xml")
    public void testRespond_responseAlreadyExistsAndIsMatch() throws Exception {
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "2")
                .param("response", "YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        mockMvc.perform(put("/api/user/2/responses").with(bearerToken(2L))
                .param("toUserId", "1")
                .param("response", "YES"))
                .andExpect(status().isOk());
        flushPersistenceContext();
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "2")
                .param("response", "NO"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/user/1/responses").with(bearerToken(1L))
                .param("toUserId", "2")
                .param("response", "YES"))
                .andExpect(status().isConflict());
    }

    @Test
    @DatabaseSetup(value = "userResponseTest.xml")
    public void testRespond_getResponses() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/4/responses?size=2").with(bearerToken(4L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        PagedResources<UserResponseResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(2, resources.getContent().size());
        assertEquals(2, resources.getMetadata().getTotalPages());
        assertEquals(3, resources.getMetadata().getTotalElements());
    }

}
