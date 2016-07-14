package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.Application;
import com.artur.dualpair.server.OAuthHelper;
import com.artur.dualpair.server.interfaces.dto.MatchDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@DatabaseSetup({"socionicsData.xml"})
public class ITMatchControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private OAuthHelper helper;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testNext_unauthorized() throws Exception {
        mockMvc.perform(get("/api/match/next"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_noMatches.xml")
    public void testNext_noMatches() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("No matches", content);
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
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals("Artur", matchDTO.getUser().getName());
        assertEquals("Diana", matchDTO.getOpponent().getName());
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
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals("Artur", matchDTO.getUser().getName());
        assertEquals("Diana", matchDTO.getOpponent().getName());
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
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals("Diana", matchDTO.getUser().getName());
        assertEquals("Artur", matchDTO.getOpponent().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_maleAll_male.xml")
    public void testNext_gender_maleAll_male() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals("Artur", matchDTO.getUser().getName());
        assertEquals("Stephen", matchDTO.getOpponent().getName());
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
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals("Artur", matchDTO.getUser().getName());
        assertEquals("Diana", matchDTO.getOpponent().getName());
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
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals("Diana", matchDTO.getUser().getName());
        assertEquals("Artur", matchDTO.getOpponent().getName());
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
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals("Diana", matchDTO.getUser().getName());
        assertEquals("Linda", matchDTO.getOpponent().getName());
    }

    @Test
    @DatabaseSetup(value = "matchTest_next_multiple.xml")
    public void testNext_multiple() throws Exception {
        doTestMultiple("Artur", "Diana");
        doTestMultiple("Artur", "Melanie");
        doTestMultiple("Artur", "Stephanie");
    }

    private void doTestMultiple(String expectedUser, String expectedOpponent) throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/next").with(bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotEquals("No matches", content);
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals(expectedUser, matchDTO.getUser().getName());
        assertEquals(expectedOpponent, matchDTO.getOpponent().getName());
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
        mockMvc.perform(post("/api/match/1/response?response=YES").with(bearerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_undefinedToYes() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        mockMvc.perform(post("/api/match/1/response?response=YES").with(bearerToken))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/api/match/1"));
        flushPersistenceContext();
        jdbcTemplate.query("select response from matches where user_id=1", rs -> {
            assertEquals("2", rs.getString("response"));
        });
    }

    @Test
    @DatabaseSetup(value = "matchTest_response.xml")
    public void testResponse_undefinedToNo() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        mockMvc.perform(post("/api/match/1/response?response=NO").with(bearerToken))
                 .andExpect(status().isSeeOther())
                 .andExpect(header().string("Location", "/api/match/1"));
        flushPersistenceContext();
        jdbcTemplate.query("select response from matches where user_id=1", rs -> {
            assertEquals("1", rs.getString("response"));
        });
    }

    @Test
    public void testMatch_unauthorized() throws Exception {
        mockMvc.perform(get("/api/match/1"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "matchTest_match.xml")
    public void testMatch_forbidden() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(2L));
        mockMvc.perform(get("/api/match/1").with(bearerToken))
          .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "matchTest_match.xml")
    public void testMatch() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L));
        MvcResult result = mockMvc.perform(get("/api/match/1").with(bearerToken))
          .andExpect(status().isOk())
          .andReturn();
        String content = result.getResponse().getContentAsString();
        MatchDTO matchDTO = new ObjectMapper().readValue(content, MatchDTO.class);
        assertEquals("Artur", matchDTO.getUser().getName());
        assertEquals("Linda", matchDTO.getOpponent().getName());
    }

    private void flushPersistenceContext() {
        EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory).flush();
    }
}