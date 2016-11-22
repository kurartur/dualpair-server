package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.user.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.vkontakte.api.IUsersOperations;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteDate;
import org.springframework.social.vkontakte.api.VKontakteProfile;

import java.text.SimpleDateFormat;

import static org.junit.Assert.*;
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
        when(connection.fetchUserProfile()).thenReturn(new UserProfile(null, null, null, null, "mail@mail.com", null));
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
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setFirstName("firstName");
        vKontakteProfile.setGender("1");
        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 1990));
        when(usersOperations.getUser("sex, bdate")).thenReturn(vKontakteProfile);
        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals("firstName", user.getName());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1990-02-01"), user.getDateOfBirth());
        assertEquals(User.Gender.FEMALE, user.getGender());
        assertEquals("mail@mail.com", user.getEmail());
    }

    @Test
    public void enhanceUser_sex() throws Exception {
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setGender("1");
        when(usersOperations.getUser("sex, bdate")).thenReturn(vKontakteProfile);
        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(User.Gender.FEMALE, user.getGender());

        vKontakteProfile.setGender("2");
        user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(User.Gender.MALE, user.getGender());

        vKontakteProfile.setGender(null);
        try {
            user = vKontakteDataProvider.enhanceUser(new User());
            fail();
        } catch (SocialDataException sde) {
            assertTrue(sde.getMessage().contains("Invalid gender"));
        }
    }

    @Test
    public void enhanceUser_bDate() throws Exception {
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setGender("1");
        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 1990));
        when(usersOperations.getUser("sex, bdate")).thenReturn(vKontakteProfile);
        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1990-02-01"), user.getDateOfBirth());

        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 0));
        user = vKontakteDataProvider.enhanceUser(new User());
        assertNull(user.getDateOfBirth());
    }

    @Test
    public void getPhotos() throws Exception {

    }

    @Test
    public void getPhoto() throws Exception {

    }

}