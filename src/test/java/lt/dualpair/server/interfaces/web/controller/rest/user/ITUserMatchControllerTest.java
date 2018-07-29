package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.match.MatchResource;
import lt.dualpair.server.interfaces.resource.user.UserAccountResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup("userMatch.xml")
public class ITUserMatchControllerTest extends BaseRestControllerTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT")); // TODO set system timezone
    }

    @Test
    public void testMatches() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches?timestamp=1472087710").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(6, resources.getContent().size());
        assertEquals(1, resources.getMetadata().getTotalPages());
        assertEquals(6, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/matches?timestamp=1472087710"));
        assertNull(resources.getLink("next"));
    }

    @Test
    public void testMatches_paged() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches?timestamp=1472087710&size=2").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(2, resources.getContent().size());
        assertEquals(3, resources.getMetadata().getTotalPages());
        assertEquals(6, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&size=2"));
        assertTrue(resources.getLink("next").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&page=1&size=2"));
    }

    @Test
    public void testMatches_timestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches?timestamp=1472087705").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(5, resources.getContent().size());
        assertEquals(1, resources.getMetadata().getTotalPages());
        assertEquals(5, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/matches?timestamp=1472087705"));
        assertNull(resources.getLink("next"));
    }

    @Test
    public void testGetMutualMatch_accountsAreSet() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches/1").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        MatchResource resource = new ObjectMapper().readValue(result.getResponse().getContentAsString(), MatchResource.class);
        List<UserAccountResource> accounts = resource.getUser().getAccounts();
        UserAccountResource userAccountResource = accounts.iterator().next();
        assertEquals("FB", userAccountResource.getAccountType());
        assertEquals("100", userAccountResource.getAccountId());
    }

    @Test
    public void testGetMatch_notFound() throws Exception {
        mockMvc.perform(get("/api/user/1/matches/7").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void testGetMatch_invalidUser() throws Exception {
        mockMvc.perform(get("/api/user/1/matches/6").with(bearerToken(2L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andReturn();
    }

    @Test
    public void testUnmatch_forbidden() throws Exception {
        mockMvc.perform(delete("/api/user/1/matches/6").with(bearerToken(2L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andReturn();
    }

    @Test
    public void testUnmatch() throws Exception {
        mockMvc.perform(delete("/api/user/1/matches/6").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        flushPersistenceContext();
        Map<String, Object> userResponse = jdbcTemplate.queryForMap("select * from user_responses where user_id=1 and to_user_id=7");
        assertEquals("N", userResponse.get("response"));
        assertNull(userResponse.get("match_id"));
        Map<String, Object> opponentResponse = jdbcTemplate.queryForMap("select * from user_responses where user_id=7 and to_user_id=1");
        assertEquals("Y", opponentResponse.get("response"));
        assertNull(opponentResponse.get("match_id"));
        List<Long> matches = jdbcTemplate.queryForList("select * from matches where id=6", Long.class);
        assertTrue(matches.isEmpty());
    }
}
