package lt.dualpair.server.service.user;

import com.vk.api.sdk.actions.Photos;
import com.vk.api.sdk.actions.Users;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.base.Sex;
import com.vk.api.sdk.objects.photos.responses.GetResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.photos.PhotosGetQuery;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.users.UsersGetQuery;
import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VKontakteDataProviderTest {

    private VKontakteDataProvider vKontakteDataProvider;
    private Connection connection = mock(Connection.class);
    private VkApiClient vkApiClient = mock(VkApiClient.class);
    private Photos photos = mock(Photos.class);
    private Users users = mock(Users.class);

    @Before
    public void setUp() throws Exception {
        when(connection.createData()).thenReturn(new ConnectionData("vkontakte", "1", null, null, null, null, null, null, null));
        when(vkApiClient.photos()).thenReturn(photos);
        when(vkApiClient.users()).thenReturn(users);
        when(connection.fetchUserProfile()).thenReturn(new UserProfile(null, null, null, null, "mail@mail.com", null));
        vKontakteDataProvider = new VKontakteDataProvider(connection) {
            @Override
            protected VkApiClient createVpApiClient() {
                return vkApiClient;
            }
        };

        List<com.vk.api.sdk.objects.photos.Photo> vkPhotos = Arrays.asList(mockVkPhoto(10, "src"));
        PhotosGetQuery query = mock(PhotosGetQuery.class);
        GetResponse response = mock(GetResponse.class);
        when(query.execute()).thenReturn(response);
        when(query.albumId(any(String.class))).thenReturn(query);
        when(query.count(any(Integer.class))).thenReturn(query);
        when(query.rev(true)).thenReturn(query);
        when(photos.get(any(UserActor.class))).thenReturn(query);
        when(response.getItems()).thenReturn(vkPhotos);
    }

    @Test
    public void getAccountId() throws Exception {
        assertEquals("1", vKontakteDataProvider.getAccountId());
    }

    @Test
    public void enhanceUser() throws Exception {
        UserXtrCounters vkUser = mockVkUser("firstName", Sex.FEMALE, "1.2.1990");
        UsersGetQuery usersQueryMock = mock(UsersGetQuery.class);
        when(users.get(any(UserActor.class))).thenReturn(usersQueryMock);
        when(usersQueryMock.fields(UserField.SEX, UserField.BDATE)).thenReturn(usersQueryMock);
        when(usersQueryMock.execute()).thenReturn(Arrays.asList(vkUser));

        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals("firstName", user.getName());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1990-02-01"), user.getDateOfBirth());
        assertEquals(Gender.FEMALE, user.getGender());
        Photo photo = user.getPhotos().get(0);
        assertEquals("src", photo.getSourceLink());
        assertEquals(user, photo.getUser());
    }

    @Test
    public void enhanceUser_sex() throws Exception {
        UserXtrCounters vkUser = mockVkUser("firstName", Sex.FEMALE, "1.2.1990");
        UsersGetQuery usersQueryMock = mock(UsersGetQuery.class);
        when(users.get(any(UserActor.class))).thenReturn(usersQueryMock);
        when(usersQueryMock.fields(UserField.SEX, UserField.BDATE)).thenReturn(usersQueryMock);
        when(usersQueryMock.execute()).thenReturn(Arrays.asList(vkUser));

        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(Gender.FEMALE, user.getGender());

        when(vkUser.getSex()).thenReturn(Sex.MALE);
        user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(Gender.MALE, user.getGender());

        when(vkUser.getSex()).thenReturn(Sex.UNKNOWN);
        user = vKontakteDataProvider.enhanceUser(new User());
        assertNull(user.getGender());
    }

    @Test
    public void enhanceUser_bDate() throws Exception {
        UserXtrCounters vkUser = mockVkUser("firstName", Sex.FEMALE, "1.2.1990");
        UsersGetQuery usersQueryMock = mock(UsersGetQuery.class);
        when(users.get(any(UserActor.class))).thenReturn(usersQueryMock);
        when(usersQueryMock.fields(UserField.SEX, UserField.BDATE)).thenReturn(usersQueryMock);
        when(usersQueryMock.execute()).thenReturn(Arrays.asList(vkUser));

        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1990-02-01"), user.getDateOfBirth());

        when(vkUser.getBdate()).thenReturn("1.2.0");
        user = vKontakteDataProvider.enhanceUser(new User());
        assertNull(user.getDateOfBirth());
    }

    private com.vk.api.sdk.objects.photos.Photo mockVkPhoto(Integer id, String src) {
        com.vk.api.sdk.objects.photos.Photo vkPhoto = mock(com.vk.api.sdk.objects.photos.Photo.class);
        when(vkPhoto.getPhoto604()).thenReturn(src);
        when(vkPhoto.getId()).thenReturn(id);
        return vkPhoto;
    }

    private com.vk.api.sdk.objects.photos.PhotoXtrRealOffset mockVkPhotoXtrRealOffset(Integer id, String src) {
        com.vk.api.sdk.objects.photos.PhotoXtrRealOffset vkPhoto = mock(com.vk.api.sdk.objects.photos.PhotoXtrRealOffset.class);
        when(vkPhoto.getPhoto604()).thenReturn(src);
        when(vkPhoto.getId()).thenReturn(id);
        return vkPhoto;
    }

    private UserXtrCounters mockVkUser(String firstName, Sex sex, String bdate) {
        UserXtrCounters user = mock(UserXtrCounters.class);
        when(user.getFirstName()).thenReturn(firstName);
        when(user.getSex()).thenReturn(sex);
        when(user.getBdate()).thenReturn(bdate);
        return user;
    }

}