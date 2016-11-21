package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResource;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DatabaseSetup({"../socionicsData.xml", "userTest.xml"})
public class ITUserControllerTest extends BaseRestControllerTest {

    @Test
    public void testGetUser_unauthorized() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMe() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/me").with(bearerToken(1L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserResource userResource = objectMapper.readValue(content, UserResource.class);
        assertEquals("Artur", userResource.getName());
    }

    @Test
    public void testUpdateUser() throws Exception {
        String content = "{ \"description\": \"descr\"}";
        mockMvc.perform(
                    patch("/api/user/1")
                        .with(bearerToken(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testSetSociotypes_unauthorized() throws Exception {
        mockMvc.perform(post("/api/user/sociotypes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup("userTest_setSociotypes.xml")
    public void testSetSociotypes_ok() throws Exception {
        String data = "[\"EII\"]";
        mockMvc.perform(put("/api/user/1/sociotypes")
                    .with(bearerToken(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(data.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/user/1"))
                .andExpect(content().string(""));
        flushPersistenceContext();
        Map<String, Object> rs = jdbcTemplate.queryForMap(
                "select s.code1 as code from users_sociotypes us " +
                "inner join sociotypes s on s.id = us.sociotype_id " +
                "where us.user_id=1");
        assertEquals("EII", rs.get("code"));
    }

    @Test
    @DatabaseSetup("userTest_setSociotypes.xml")
    public void testSetSociotypes_invalidUser() throws Exception {
        String data = "[\"EII\"]";
        mockMvc.perform(put("/api/user/1/sociotypes")
                .with(bearerToken(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.getBytes()))
                .andExpect(status().isForbidden());
        Integer c = jdbcTemplate.queryForObject("select count(*) from users_sociotypes where user_id=1", Integer.class);
        assertTrue(c.equals(0));
    }

    @Test
    @DatabaseSetup("userTest_setSociotypes.xml")
    public void testSetSociotypes_noCodes() throws Exception {
        String data = "[]";
        mockMvc.perform(put("/api/user/1/sociotypes")
                      .with(bearerToken(1L))
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(data.getBytes()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("{\"statusCode\":400,\"message\":\"Invalid sociotype code count. Must be 1 or 2\"}"));
    }

    @Test
    @DatabaseSetup("userTest_setSociotypes.xml")
    public void testSetSociotypes_matchRemoval() throws Exception {
        String data = "[\"ILE\", \"LSI\"]";
        mockMvc.perform(put("/api/user/2/sociotypes")
                        .with(bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/user/2"))
                .andExpect(content().string(""));
        flushPersistenceContext();
        Integer c = jdbcTemplate.queryForObject("select count(*) from matches where id in (1, 2)", Integer.class);
        assertEquals((Integer)0, c);
        c = jdbcTemplate.queryForObject("select count(*) from matches where id in (3, 4)", Integer.class);
        assertEquals((Integer)2, c);
    }

    @Test
    @DatabaseSetup("userTest_setDateOfBirth.xml")
    public void testSetDateOfBirth() throws Exception {
        mockMvc.perform(put("/api/user/1/date-of-birth?dateOfBirth=1990-02-03")
                    .with(bearerToken(1L)))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/api/user"))
                .andExpect(content().string(""));
        flushPersistenceContext();
        Date persistedDate = jdbcTemplate.queryForObject("select date_of_birth from users where id=1", Date.class);
        assertEquals(Date.from(LocalDate.of(1990, 2, 3).atStartOfDay(ZoneId.systemDefault()).toInstant()), persistedDate);
    }

    @Test
    @DatabaseSetup("userTest_setDateOfBirth.xml")
    public void testSetDateOfBirth_invalidUser() throws Exception {
        mockMvc.perform(put("/api/user/1/date-of-birth?dateOfBirth=1990-02-03")
                .with(bearerToken(2L)))
                .andExpect(status().isForbidden());
        Date date = jdbcTemplate.queryForObject("select date_of_birth from users where id=1", Date.class);
        assertNull(date);
    }

    @Test
    @DatabaseSetup("userTest_searchParameters.xml")
    public void testSetSearchParameters_noParameters() throws Exception {
        doTestSetSearchParameters(1L);
    }

    @Test
    @DatabaseSetup("userTest_searchParameters.xml")
    public void testSetSearchParameters_parametersExist() throws Exception {
        doTestSetSearchParameters(2L);
    }

    private void doTestSetSearchParameters(Long userId) throws Exception {
        String data = "{\"searchMale\":true,\"searchFemale\":true,\"minAge\":\"20\",\"maxAge\":\"30\"}";
        mockMvc.perform(put("/api/user/" + userId + "/search-parameters")
                .with(bearerToken(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/user"))
                .andExpect(content().string(""));
        flushPersistenceContext();
        Map<String, Object> searchParameters = jdbcTemplate.queryForMap("select * from search_parameters where user_id=" + userId);
        assertFalse(searchParameters.isEmpty());
        assertEquals("Y", searchParameters.get("search_male"));
        assertEquals("Y", searchParameters.get("search_female"));
        assertEquals(20, searchParameters.get("min_age"));
        assertEquals(30, searchParameters.get("max_age"));
    }

    @Test
    @DatabaseSetup("userTest_searchParameters.xml")
    public void testSetSearchParameters_invalidUser() throws Exception {
        String data = "{\"searchMale\":true,\"searchFemale\":true,\"minAge\":\"20\",\"maxAge\":\"30\"}";
        mockMvc.perform(put("/api/user/1/search-parameters")
                .with(bearerToken(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.getBytes()))
                .andExpect(status().isForbidden());
        Integer c = jdbcTemplate.queryForObject("select count(*) from search_parameters where user_id=1", Integer.class);
        assertTrue(c.equals(0));
    }

    @Test
    @DatabaseSetup("userTest_searchParameters.xml")
    public void testGetSearchParameters_invalidUser() throws Exception {
        mockMvc.perform(get("/api/user/1/search-parameters")
                .with(bearerToken(2L)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup("userTest_searchParameters.xml")
    public void testGetSearchParameters() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/2/search-parameters")
                .with(bearerToken(2L)))
                .andExpect(status().isOk())
                .andReturn();
        SearchParametersResource resource = new ObjectMapper().readValue(result.getResponse().getContentAsString(), SearchParametersResource.class);
        assertEquals((Integer)20, resource.getMinAge());
        assertEquals((Integer)30, resource.getMaxAge());
        assertFalse(resource.getSearchFemale());
        assertFalse(resource.getSearchMale());
    }

    @Test
    @DatabaseSetup("userTest_setLocation.xml")
    public void testSetLocation() throws Exception {
        String data = "{\"latitude\":54.63, \"longitude\":25.32}";
        mockMvc.perform(put("/api/user/1/locations")
                    .with(bearerToken(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(data.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/user"))
                .andExpect(content().string(""));
        Map<String, Object> locations = jdbcTemplate.queryForMap("select * from user_locations where user_id=1");
        assertFalse(locations.isEmpty());
        assertEquals(54.63, locations.get("latitude"));
        assertEquals(25.32, locations.get("longitude"));
        assertEquals("LT", locations.get("country_code"));
        assertEquals("Vilnius", locations.get("city"));
    }

    @Test
    @DatabaseSetup("userTest_setLocation.xml")
    public void testSetLocation_oldLocationsDeleted() throws Exception {
        for (double i = 0.0; i <= 10.0; i += 1.0) {
            putLocation(i, i);
        }
        List<Map<String, Object>> locations = jdbcTemplate.queryForList("select * from user_locations where user_id=1 order by id asc");
        assertEquals(5, locations.size());
        double latLon = 6;
        for (Map<String, Object> map : locations) {
            assertEquals(latLon, map.get("latitude"));
            assertEquals(latLon, map.get("longitude"));
            latLon += 1.0;
        }
    }

    private void putLocation(Double latitude, Double longitude) throws Exception {
        String data = "{\"latitude\": " + latitude.toString() + ", \"longitude\": " + longitude.toString() + "}";
        mockMvc.perform(put("/api/user/1/locations")
                .with(bearerToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.getBytes())).andExpect(status().isCreated());
        flushPersistenceContext();
    }

    @Test
    @DatabaseSetup("userTest_setLocation.xml")
    public void testSetLocation_invalidUser() throws Exception {
        String data = "{\"latitude\":54.63, \"longitude\":25.32}";
        mockMvc.perform(put("/api/user/1/locations")
                .with(bearerToken(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.getBytes()))
                .andExpect(status().isForbidden());
        Integer c = jdbcTemplate.queryForObject("select count(*) from user_locations where user_id=1", Integer.class);
        assertTrue(c.equals(0));
    }

}