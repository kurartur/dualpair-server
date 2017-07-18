package lt.dualpair.server.interfaces.web.controller.rest;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import lt.dualpair.server.Application;
import lt.dualpair.server.OAuthHelper;
import lt.dualpair.server.security.UserDetailsImpl;
import org.junit.Before;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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
public abstract class BaseRestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @Autowired
    protected OAuthHelper helper;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    protected void flushPersistenceContext() {
        EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory).flush();
    }

    protected RequestPostProcessor bearerToken(Long userId) {
        return helper.bearerToken("dualpairandroid", new UserDetailsImpl(OAuthHelper.buildUserPrincipal(userId)));
    }

}
