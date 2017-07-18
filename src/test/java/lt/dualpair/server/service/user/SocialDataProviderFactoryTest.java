package lt.dualpair.server.service.user;

import lt.dualpair.core.user.UserAccount;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
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
    public void testGetProvider_nullParameters() throws Exception {
        try {
            socialDataProviderFactory.getProvider(null, 1L);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Account type required", iae.getMessage());
        }

        try {
            socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("User id required", iae.getMessage());
        }
    }

    @Test
    public void testGetProvider_facebook() throws Exception {
        ConnectionRepository connectionRepository = mock(ConnectionRepository.class);
        doReturn(connectionRepository).when(usersConnectionRepository).createConnectionRepository("1");
        SocialDataProvider provider = socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, 1L);
        assertTrue(provider instanceof FacebookDataProvider);
        verify(connectionRepository, times(1)).findPrimaryConnection(Facebook.class);
    }

    @Test
    public void testGetProvider_vkontakte() throws Exception {
        ConnectionRepository connectionRepository = mock(ConnectionRepository.class);
        doReturn(connectionRepository).when(usersConnectionRepository).createConnectionRepository("1");
        Connection connection = mock(Connection.class);
        doReturn(connection).when(connectionRepository).findPrimaryConnection(VKontakte.class);
        doReturn(new ConnectionData("vkontakte", "1", null, null, null, null, null, null, null)).when(connection).createData();
        SocialDataProvider provider = socialDataProviderFactory.getProvider(UserAccount.Type.VKONTAKTE, 1L);
        assertTrue(provider instanceof VKontakteDataProvider);
        verify(connectionRepository, times(1)).findPrimaryConnection(VKontakte.class);
    }

}