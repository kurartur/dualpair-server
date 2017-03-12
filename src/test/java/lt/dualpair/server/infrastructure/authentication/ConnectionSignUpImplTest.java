package lt.dualpair.server.infrastructure.authentication;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.user.Gender;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import lt.dualpair.server.service.user.FacebookDataProvider;
import lt.dualpair.server.service.user.SocialDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookAdapter;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ConnectionSignUpImplTest {

    private ConnectionSignUpImpl connectionSignUp = new ConnectionSignUpImpl() {
        @Override
        protected SocialDataProvider getProvider(Connection connection) {
            return socialDataProvider;
        }
    };
    private SocialDataProvider socialDataProvider = mock(FacebookDataProvider.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() throws Exception {
        connectionSignUp.setUserRepository(userRepository);
    }

    @Test
    public void testExecute_nullParameters() throws Exception {
        try {
            connectionSignUp.execute(null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Connection required", iae.getMessage());
        }
    }

    @Test
    public void testExecute() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.getApi()).thenReturn(mock(Facebook.class));
        Date dateOfBirth = Date.from(LocalDate.now().minus(5, ChronoUnit.YEARS).atStartOfDay(ZoneId.systemDefault()).toInstant());
        when(socialDataProvider.enhanceUser(any(User.class))).thenAnswer(invocation -> {
            User user = (User)invocation.getArguments()[0];
            user.setDateOfBirth(dateOfBirth);
            user.setGender(Gender.MALE);
            return user;
        });
        when(socialDataProvider.getAccountId()).thenReturn("111");

        String username = connectionSignUp.execute(connection);

        assertFalse(StringUtils.isEmpty(username));
        verify(socialDataProvider, times(1)).enhanceUser(any(User.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User resultUser = userCaptor.getValue();

        assertEquals(1, resultUser.getUserAccounts().size());
        UserAccount userAccount = resultUser.getUserAccounts().iterator().next();
        assertEquals("111", userAccount.getAccountId());
        assertEquals(UserAccount.Type.FACEBOOK, userAccount.getAccountType());
        assertNotNull(resultUser.getUserId());
        assertNotNull(resultUser.getDateCreated());
        assertNotNull(resultUser.getDateUpdated());
        SearchParameters searchParameters = resultUser.getSearchParameters();
        assertNotNull(searchParameters);
        assertEquals((Integer)2, searchParameters.getMinAge());
        assertEquals((Integer)8, searchParameters.getMaxAge());
        assertTrue(searchParameters.getSearchFemale());
        assertFalse(searchParameters.getSearchMale());
    }

    @Test
    public void testBuildUserId() throws Exception {
        String hash = connectionSignUp.buildUserId("mymail@google.com", 123456789L);
        assertEquals("fb77d8e5c02d0dac8d7d12bcdff36f5a", hash);
    }

    @Test
    public void testGetProvider_connection_nullParameters() throws Exception {
        ConnectionSignUpImpl connectionSignUp = new ConnectionSignUpImpl();
        try {
            connectionSignUp.getProvider(null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Connection required", iae.getMessage());
        }
    }

    @Test
    public void testGetProvider_connection_facebook() throws Exception {
        ConnectionSignUpImpl connectionSignUp = new ConnectionSignUpImpl();
        FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider("appId", "appSecret", "appNamespace");
        SocialDataProvider socialDataProvider = connectionSignUp.getProvider(new OAuth2Connection<>(null, null, null, null, null, facebookServiceProvider, mock(FacebookAdapter.class)));
        assertTrue(socialDataProvider instanceof FacebookDataProvider);
    }

}