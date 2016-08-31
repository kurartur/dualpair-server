package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.match.MatchResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

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
        MvcResult result = mockMvc.perform(get("/api/user/1/mutual-matches?timestamp=1472087710").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(5, resources.getContent().size());
        assertEquals(1, resources.getMetadata().getTotalPages());
        assertEquals(5, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/mutual-matches?timestamp=1472087710"));
        assertNull(resources.getLink("next"));
    }

    @Test
    public void testGetMutualMatches_paged() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/mutual-matches?timestamp=1472087710&size=2").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(2, resources.getContent().size());
        assertEquals(3, resources.getMetadata().getTotalPages());
        assertEquals(5, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/mutual-matches?timestamp=1472087710&size=2"));
        assertTrue(resources.getLink("next").getHref().endsWith("/api/user/1/mutual-matches?timestamp=1472087710&page=1&size=2"));

        result = mockMvc.perform(get(resources.getLink("next").getHref()).with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(2, resources.getContent().size());
        assertEquals(3, resources.getMetadata().getTotalPages());
        assertEquals(5, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("prev").getHref().endsWith("/api/user/1/mutual-matches?timestamp=1472087710&page=0&size=2"));
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/mutual-matches?timestamp=1472087710&page=1&size=2"));
        assertTrue(resources.getLink("next").getHref().endsWith("/api/user/1/mutual-matches?timestamp=1472087710&page=2&size=2"));
    }

    @Test
    public void testGetMutualMatches_timestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/1/mutual-matches?timestamp=1472087705").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedResources<MatchResource> resources = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PagedResources.class);
        assertEquals(4, resources.getContent().size());
        assertEquals(1, resources.getMetadata().getTotalPages());
        assertEquals(4, resources.getMetadata().getTotalElements());
        assertTrue(resources.getLink("self").getHref().endsWith("/api/user/1/mutual-matches?timestamp=1472087705"));
        assertNull(resources.getLink("next"));
    }
}