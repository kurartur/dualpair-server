package lt.dualpair.server.interfaces.web.controller.rest.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.match.MatchResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup({"../socionicsData.xml"})
public class ITMatchControllerTest extends BaseRestControllerTest {

    @Test
    public void testNext_unauthorized() throws Exception {
        mockMvc.perform(get("/api/match/next"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_noMatches.xml")
    public void testNext_noMatches() throws Exception {
        mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=N").with(bearerToken(1L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DatabaseSetup(value = "matchTest_preciseAge.xml")
    public void testNext_preciseAge() throws Exception {
        doTestAge(25, 25);
    }

    @Test
    @DatabaseSetup(value = "matchTest_ageRange1.xml")
    public void testNext_ageRange1() throws Exception {
        doTestAge(25, 30);
    }

    @Test
    @DatabaseSetup(value = "matchTest_ageRange2.xml")
    public void testNext_ageRange2() throws Exception {
        doTestAge(20, 25);
    }

    @Test
    @DatabaseSetup(value = "matchTest_ageRange3.xml")
    public void testNext_ageRange3() throws Exception {
        doTestAge(20, 30);
    }

    private void doTestAge(int minAge, int maxAge) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/match/next?mia=" + minAge + "&maa=" + maxAge + "&sf=Y&sm=N").with(bearerToken(1L)))
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
        MvcResult result = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=N").with(bearerToken(1L)))
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
        MvcResult result = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=N&sm=Y").with(bearerToken(10L)))
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
        MvcResult result = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=Y").with(bearerToken(1L)))
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
        MvcResult result = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=Y").with(bearerToken(1L)))
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
        MvcResult result = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=Y").with(bearerToken(10L)))
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
        MvcResult result = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=Y").with(bearerToken(10L)))
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
        String content = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=N").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content,"Diana");

        content = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=N").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Diana");
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_inRepo.xml")
    public void testNext_inRepo_excludeOpponents() throws Exception {
        String content = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=N&exo=2,3,5").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Stephanie");

        content = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=N&exo=2,3,4").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Lucie");
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_inRepo.xml")
    public void testNext_inRepo_deleteInvalid() throws Exception {
        String content = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=N&exo=2,3,4").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Lucie");
        assertEquals((Integer)0, jdbcTemplate.queryForObject("select count(*) from matches where id=5", Integer.class));
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_noDuplicates.xml")
    public void textNext_noDuplicates() throws Exception {
        String content = mockMvc.perform(get("/api/match/next?mia=25&maa=25&sf=Y&sm=N").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Melanie");
    }

    private void assertMatch(String content, String expectedOpponent) throws IOException {
        MatchResource matchResource = new ObjectMapper().readValue(content, MatchResource.class);
        assertTrue(matchResource.getUser().getLink("user").getHref().endsWith("/me"));
        assertEquals(expectedOpponent, matchResource.getOpponent().getUser().getName());
    }

}