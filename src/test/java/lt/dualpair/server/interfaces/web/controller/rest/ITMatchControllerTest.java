package lt.dualpair.server.interfaces.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.match.MatchResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup({"socionicsData.xml"})
public class ITMatchControllerTest extends BaseRestControllerTest {

    @Test
    public void testNext_unauthorized() throws Exception {
        mockMvc.perform(get("/api/match/next"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_noMatches.xml")
    public void testNext_noMatches() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DatabaseSetup(value = "matchTest_preciseAge.xml")
    public void testNext_preciseAge() throws Exception {
        doTestAge();
    }

    @Test
    @DatabaseSetup(value = "matchTest_ageRange1.xml")
    public void testNext_ageRange1() throws Exception {
        doTestAge();
    }

    @Test
    @DatabaseSetup(value = "matchTest_ageRange2.xml")
    public void testNext_ageRange2() throws Exception {
        doTestAge();
    }

    @Test
    @DatabaseSetup(value = "matchTest_ageRange3.xml")
    public void testNext_ageRange3() throws Exception {
        doTestAge();
    }

    private void doTestAge() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals("Diana", matchResource.getOpponent().getUser().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_maleFemale.xml")
    public void testNext_gender_maleFemale() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals("Diana", matchResource.getOpponent().getUser().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_femaleMale.xml")
    public void testNext_gender_femaleMale() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(10L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals("Artur", matchResource.getOpponent().getUser().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_maleAll_male.xml")
    public void testNext_gender_maleAll_male() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals("Stephen", matchResource.getOpponent().getUser().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_maleAll_female.xml")
    public void testNext_gender_maleAll_female() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals("Diana", matchResource.getOpponent().getUser().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_femaleAll_male.xml")
    public void testNext_gender_femaleAll_male() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(10L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals("Artur", matchResource.getOpponent().getUser().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_femaleAll_female.xml")
    public void testNext_gender_femaleAll_female() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(10L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals("Linda", matchResource.getOpponent().getUser().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_inRepo.xml")
    public void testNext_inRepo() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        String content = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content,"Diana");

        content = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Diana");
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_inRepo.xml")
    public void testNext_inRepo_excludeOpponents() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        String content = mockMvc.perform(get("/api/match/next?exclopp[]=2&exclopp[]=3").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Stephanie");

        content = mockMvc.perform(get("/api/match/next?exclopp[]=2&exclopp[]=3&exclopp[]=4").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Lucie");
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_noDuplicates.xml")
    public void textNext_noDuplicates() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        String content = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Melanie");
    }

    private void assertMatch(String content, String expectedOpponent) throws IOException {
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals(expectedOpponent, matchResource.getOpponent().getUser().getName());
    }

    @Test
    public void testResponse_unauthorized() throws Exception {
        mockMvc.perform(post("/api/match/1/response?response=YES"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_invalidUser() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(3L));
        mockMvc.perform(put("/api/match/1/response").with(bearerToken).content("YES"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_undefinedToYes() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        mockMvc.perform(put("/api/match/1/response").with(bearerToken).content("YES"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/api/match/1"));
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap("select response from match_parties where user_id=1");
        assertEquals(2, rs.get("response"));
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_undefinedToNo() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        mockMvc.perform(put("/api/match/1/response").with(bearerToken).content("NO"))
                 .andExpect(status().isSeeOther())
                 .andExpect(header().string("Location", "/api/match/1"));
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap("select response from match_parties where user_id=1");
        assertEquals(1, rs.get("response"));
    }

    @Test
    public void testMatch_unauthorized() throws Exception {
        mockMvc.perform(get("/api/match/1"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "matchTest_match.xml")
    public void testMatch_forbidden() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(3L));
        mockMvc.perform(get("/api/match/1").with(bearerToken))
          .andExpect(status().isNotFound());
    }

    @Test
    @DatabaseSetup(value = "matchTest_match.xml")
    public void testMatch() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/1").with(bearerToken))
          .andExpect(status().isOk())
          .andReturn();
        String content = result.getResponse().getContentAsString();
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals("Linda", matchResource.getOpponent().getUser().getName());
    }
}