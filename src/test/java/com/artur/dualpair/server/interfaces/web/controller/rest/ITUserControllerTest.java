package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.Application;
import com.artur.dualpair.server.OAuthHelper;
import com.artur.dualpair.server.interfaces.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
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

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void testGetUser() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        MvcResult result = mockMvc.perform(get("/api/user").with(bearerToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDTO = objectMapper.readValue(content, UserDTO.class);
        assertEquals("Artur", userDTO.getName());
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
        String data = "[{\"code1\": \"EII\"}]";
        mockMvc.perform(post("/api/user/sociotypes")
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
    public void testSetSociotypes_noCodes() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        String data = "[]";
        mockMvc.perform(post("/api/user/sociotypes")
                      .with(bearerToken)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(data.getBytes()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("{\"error_id\":\"Invalid sociotype code count. Must be 1 or 2\",\"error_description\":\"Invalid sociotype code count. Must be 1 or 2\"}"));
    }

    @Test
    @DatabaseSetup("userTest_setDateOfBirth.xml")
    public void testSetDateOfBirth() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        mockMvc.perform(post("/api/user/date-of-birth?dateOfBirth=1990-02-03")
                    .with(bearerToken))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/api/user"))
                .andExpect(content().string(""));
        flushPersistenceContext();
        Date persistedDate = jdbcTemplate.queryForObject("select date_of_birth from users where id=1", Date.class);
        assertEquals(Date.from(LocalDate.of(1990, 2, 3).atStartOfDay(ZoneId.systemDefault()).toInstant()), persistedDate);
    }

    private void flushPersistenceContext() {
        EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory).flush();
    }
}