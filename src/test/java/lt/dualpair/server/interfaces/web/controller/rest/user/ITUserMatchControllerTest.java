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
import java.util.TimeZone;

import static org.junit.Assert.*;
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
    public void testGetMutualMatches() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches?timestamp=1472087710&mt=mu").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(5, resources.getContent().size());
        assertEquals(1, resources.getMetadata().getTotalPages());
        assertEquals(5, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&mt=mu"));
        assertNull(resources.getLink("next"));
    }

    @Test
    public void testGetMutualMatches_paged() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches?timestamp=1472087710&mt=mu&size=2").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(2, resources.getContent().size());
        assertEquals(3, resources.getMetadata().getTotalPages());
        assertEquals(5, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&mt=mu&size=2"));
        assertTrue(resources.getLink("next").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&mt=mu&page=1&size=2"));

        result = mockMvc.perform(get(resources.getLink("next").getHref()).with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(2, resources.getContent().size());
        assertEquals(3, resources.getMetadata().getTotalPages());
        assertEquals(5, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("prev").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&mt=mu&page=0&size=2"));
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&mt=mu&page=1&size=2"));
        assertTrue(resources.getLink("next").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&mt=mu&page=2&size=2"));
    }

    @Test
    public void testGetMutualMatches_timestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches?timestamp=1472087705&mt=mu").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(4, resources.getContent().size());
        assertEquals(1, resources.getMetadata().getTotalPages());
        assertEquals(4, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/matches?timestamp=1472087705&mt=mu"));
        assertNull(resources.getLink("next"));
    }

    @Test
    public void testGetMutualMatch_accountsAreSet() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches/1").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        MatchResource resource = new ObjectMapper().readValue(result.getResponse().getContentAsString(), MatchResource.class);
        List<UserAccountResource> accounts = resource.getOpponent().getUser().getAccounts();
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
    public void testGetReviewedMatches() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/matches?timestamp=1472087710&mt=re").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(6, resources.getContent().size());
        assertEquals(1, resources.getMetadata().getTotalPages());
        assertEquals(6, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/matches?timestamp=1472087710&mt=re"));
        assertNull(resources.getLink("next"));
    }
}
