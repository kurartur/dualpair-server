package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.user.UserAccount;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookAdapter;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.vkontakte.api.VKontakte;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SocialDataProviderFactoryTest {

    private SocialDataProviderFactory socialDataProviderFactory = new SocialDataProviderFactory();
    private UsersConnectionRepository usersConnectionRepository = mock(UsersConnectionRepository.class);

    @Before
    public void setUp() throws Exception {
        socialDataProviderFactory.setUsersConnectionRepository(usersConnectionRepository);
    }

    @Test
    public void testGetProvider_connection_nullParameters() throws Exception {
        try {
            socialDataProviderFactory.getProvider(null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Connection required", iae.getMessage());
        }
    }

    @Test
    public void testGetProvider_connection_facebook() throws Exception {
        FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider("appId", "appSecret", "appNamespace");
        SocialDataProvider socialDataProvider = socialDataProviderFactory.getProvider(new OAuth2Connection<>(null, null, null, null, null, facebookServiceProvider, mock(FacebookAdapter.class)));
        assertTrue(socialDataProvider instanceof FacebookDataProvider);
    }

    @Test
    public void testGetProvider_nullParameters() throws Exception {
        try {
            socialDataProviderFactory.getProvider(null, "username");
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Account type required", iae.getMessage());
        }

        try {
            socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Username required", iae.getMessage());
        }
    }

    @Test
    public void testGetProvider_facebook() throws Exception {
        ConnectionRepository connectionRepository = mock(ConnectionRepository.class);
        doReturn(connectionRepository).when(usersConnectionRepository).createConnectionRepository("username");
        SocialDataProvider provider = socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, "username");
        assertTrue(provider instanceof FacebookDataProvider);
        verify(connectionRepository, times(1)).findPrimaryConnection(Facebook.class);
    }

    @Test
    public void testGetProvider_vkontakte() throws Exception {
        ConnectionRepository connectionRepository = mock(ConnectionRepository.class);
        doReturn(connectionRepository).when(usersConnectionRepository).createConnectionRepository("username");
        SocialDataProvider provider = socialDataProviderFactory.getProvider(UserAccount.Type.VKONTAKTE, "username");
        assertTrue(provider instanceof VKontakteDataProvider);
        verify(connectionRepository, times(1)).findPrimaryConnection(VKontakte.class);
    }

}