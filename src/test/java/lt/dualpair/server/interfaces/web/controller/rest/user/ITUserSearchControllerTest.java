package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup({"../socionicsData.xml"})
public class ITUserSearchControllerTest extends BaseRestControllerTest {

    @Test
    public void testFind_unauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_noMatches.xml")
    public void testFind_noMatches() throws Exception {
        mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=N").with(bearerToken(1L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_preciseAge.xml")
    public void testFind_preciseAge() throws Exception {
        doTestAge(25, 25);
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_ageRange1.xml")
    public void testFind_ageRange1() throws Exception {
        doTestAge(25, 30);
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_ageRange2.xml")
    public void testFind_ageRange2() throws Exception {
        doTestAge(20, 25);
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_ageRange3.xml")
    public void testFind_ageRange3() throws Exception {
        doTestAge(20, 30);
    }

    private void doTestAge(int minAge, int maxAge) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users?mia=" + minAge + "&maa=" + maxAge + "&sf=Y&sm=N").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        UserResource userResource = new ObjectMapper().readValue(content, UserResource.class);
        assertEquals("Diana", userResource.getName());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_maleFemale.xml")
    public void testFind_gender_maleFemale() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=N").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        UserResource userResource = new ObjectMapper().readValue(content, UserResource.class);
        assertEquals("Diana", userResource.getName());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_femaleMale.xml")
    public void testFind_gender_femaleMale() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=N&sm=Y").with(bearerToken(10L)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        UserResource userResource = new ObjectMapper().readValue(content, UserResource.class);
        assertEquals("Artur", userResource.getName());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_maleAll_male.xml")
    public void testFind_gender_maleAll_male() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=Y").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        UserResource userResource = new ObjectMapper().readValue(content, UserResource.class);
        assertEquals("Stephen", userResource.getName());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_maleAll_female.xml")
    public void testFind_gender_maleAll_female() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=Y").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        UserResource userResource = new ObjectMapper().readValue(content, UserResource.class);
        assertEquals("Diana", userResource.getName());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_femaleAll_male.xml")
    public void testFind_gender_femaleAll_male() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=Y").with(bearerToken(10L)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        UserResource userResource = new ObjectMapper().readValue(content, UserResource.class);
        assertEquals("Artur", userResource.getName());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_femaleAll_female.xml")
    public void testFind_gender_femaleAll_female() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=Y").with(bearerToken(10L)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        UserResource userResource = new ObjectMapper().readValue(content, UserResource.class);
        assertEquals("Linda", userResource.getName());
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_excludeOpponents.xml")
    public void testFind_excludeOpponents() throws Exception {
        String content = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=N&exo=2,3,5").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Stephanie");

        content = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=N&exo=2,3,4").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Lucie");
    }

    @Test
    @DatabaseSetup(value = "userSearchTest_noDuplicates.xml")
    public void testFind_noDuplicates() throws Exception {
        String content = mockMvc.perform(get("/api/users?mia=25&maa=25&sf=Y&sm=N").with(bearerToken(1L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertMatch(content, "Melanie");
    }

    private void assertMatch(String content, String expectedOpponent) throws IOException {
        UserResource userResource = new ObjectMapper().readValue(content, UserResource.class);
        assertEquals(expectedOpponent, userResource.getName());
    }

}