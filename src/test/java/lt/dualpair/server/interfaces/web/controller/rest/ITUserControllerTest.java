package lt.dualpair.server.interfaces.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.Application;
import lt.dualpair.server.OAuthHelper;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResource;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@Rollback
@Transactional
@DatabaseSetup("userTest.xml")
public class ITUserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private OAuthHelper helper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testGetUser_unauthorized() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMe() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        MvcResult result = mockMvc.perform(get("/api/me").with(bearerToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserResource userResource = objectMapper.readValue(content, UserResource.class);
        assertEquals("Artur", userResource.getName());
    }

    @Test
    public void testSetSociotypes_unauthorized() throws Exception {
        mockMvc.perform(post("/api/user/sociotypes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup("userTest_setSociotypes.xml")
    public void testSetSociotypes_ok() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        String data = "[\"EII\"]";
        mockMvc.perform(put("/api/user/1/sociotypes")
                    .with(bearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(data.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/user"))
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
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(2L));
        String data = "[\"EII\"]";
        mockMvc.perform(put("/api/user/1/sociotypes")
                .with(bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.getBytes()))
                .andExpect(status().isForbidden());
        Integer c = jdbcTemplate.queryForObject("select count(*) from users_sociotypes where user_id=1", Integer.class);
        assertTrue(c.equals(0));
    }

    @Test
    @DatabaseSetup("userTest_setSociotypes.xml")
    public void testSetSociotypes_noCodes() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        String data = "[]";
        mockMvc.perform(put("/api/user/1/sociotypes")
                      .with(bearerToken)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(data.getBytes()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("{\"statusCode\":400,\"message\":\"Invalid sociotype code count. Must be 1 or 2\"}"));
    }

    @Test
    @DatabaseSetup("userTest_setDateOfBirth.xml")
    public void testSetDateOfBirth() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        mockMvc.perform(put("/api/user/1/date-of-birth?dateOfBirth=1990-02-03")
                    .with(bearerToken))
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
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(2L));
        mockMvc.perform(put("/api/user/1/date-of-birth?dateOfBirth=1990-02-03")
                .with(bearerToken))
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
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(userId));
        String data = "{\"searchMale\":true,\"searchFemale\":true,\"minAge\":\"20\",\"maxAge\":\"30\"}";
        mockMvc.perform(put("/api/user/" + userId + "/search-parameters")
                .with(bearerToken)
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
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(2L));
        String data = "{\"searchMale\":true,\"searchFemale\":true,\"minAge\":\"20\",\"maxAge\":\"30\"}";
        mockMvc.perform(put("/api/user/1/search-parameters")
                .with(bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.getBytes()))
                .andExpect(status().isForbidden());
        Integer c = jdbcTemplate.queryForObject("select count(*) from search_parameters where user_id=1", Integer.class);
        assertTrue(c.equals(0));
    }

    @Test
    @DatabaseSetup("userTest_searchParameters.xml")
    public void testGetSearchParameters_invalidUser() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(2L));
        mockMvc.perform(get("/api/user/1/search-parameters")
                .with(bearerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup("userTest_searchParameters.xml")
    public void testGetSearchParameters() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(2L));
        MvcResult result = mockMvc.perform(get("/api/user/2/search-parameters")
                .with(bearerToken))
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
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        String data = "{\"latitude\":54.63, \"longitude\":25.32}";
        mockMvc.perform(put("/api/user/1/locations")
                    .with(bearerToken)
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
    public void testSetLocation_invalidUser() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(2L));
        String data = "{\"latitude\":54.63, \"longitude\":25.32}";
        mockMvc.perform(put("/api/user/1/locations")
                .with(bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.getBytes()))
                .andExpect(status().isForbidden());
        Integer c = jdbcTemplate.queryForObject("select count(*) from user_locations where user_id=1", Integer.class);
        assertTrue(c.equals(0));
    }

    private void flushPersistenceContext() {
        EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory).flush();
    }
}