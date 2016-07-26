package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.Application;
import com.artur.dualpair.server.OAuthHelper;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
@DatabaseSetup("deviceTest.xml")
public class ITDeviceControllerTest {

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

    private void flushPersistenceContext() {
        EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory).flush();
    }

    @Test
    public void testRegisterDevice() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(1L, "1"));
        mockMvc.perform(post("/api/device?id=123").with(bearerToken))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
        flushPersistenceContext();
        Map<String, Object> devices = jdbcTemplate.queryForMap("select * from user_devices where user_id=1");
        assertFalse(devices.isEmpty());
        assertEquals("123", devices.get("id"));
    }



    @Test
    public void testRegisterDevice_exists() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("dualpairandroid", helper.buildUserPrincipal(2L, "2"));
        mockMvc.perform(post("/api/device?id=600").with(bearerToken))
                .andExpect(status().isConflict())
                .andExpect(content().string(""));
        flushPersistenceContext();
        // checks if only one device exists, will throw exception if not
        Map<String, Object> devices = jdbcTemplate.queryForMap("select * from user_devices where user_id=2");
        assertFalse(devices.isEmpty());
    }
}