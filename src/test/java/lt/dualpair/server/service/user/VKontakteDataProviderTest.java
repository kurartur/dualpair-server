package lt.dualpair.server.service.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.vkontakte.api.IUsersOperations;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteProfile;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VKontakteDataProviderTest {

    private VKontakteDataProvider vKontakteDataProvider;
    private Connection<VKontakte> connection = mock(Connection.class);
    private VKontakte vkontakte = mock(VKontakte.class);
    private IUsersOperations usersOperations = mock(IUsersOperations.class);

    @Before
    public void setUp() throws Exception {
        vKontakteDataProvider = new VKontakteDataProvider(connection);
        when(connection.getApi()).thenReturn(vkontakte);
        when(vkontakte.usersOperations()).thenReturn(usersOperations);
    }

    @Test
    public void getAccountId() throws Exception {
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setId(10L);
        when(usersOperations.getUser()).thenReturn(vKontakteProfile);
        assertEquals("10", vKontakteDataProvider.getAccountId());
    }

    @Test
    public void enhanceUser() throws Exception {

    }

    @Test
    public void getPhotos() throws Exception {

    }

    @Test
    public void getPhoto() throws Exception {

    }

}