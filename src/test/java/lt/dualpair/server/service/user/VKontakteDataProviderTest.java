package lt.dualpair.server.service.user;

import com.vk.api.sdk.actions.Photos;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.photos.responses.GetAllResponse;
import com.vk.api.sdk.objects.photos.responses.GetResponse;
import com.vk.api.sdk.queries.photos.PhotosGetAllQuery;
import com.vk.api.sdk.queries.photos.PhotosGetByIdQuery;
import com.vk.api.sdk.queries.photos.PhotosGetQuery;
import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.Gender;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.vkontakte.api.IUsersOperations;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteDate;
import org.springframework.social.vkontakte.api.VKontakteProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VKontakteDataProviderTest {

    private VKontakteDataProvider vKontakteDataProvider;
    private Connection<VKontakte> connection = mock(Connection.class);
    private VKontakte vkontakte = mock(VKontakte.class);
    private IUsersOperations usersOperations = mock(IUsersOperations.class);
    private VkApiClient vkApiClient = mock(VkApiClient.class);
    private Photos photos = mock(Photos.class);

    @Before
    public void setUp() throws Exception {
        when(connection.getApi()).thenReturn(vkontakte);
        when(connection.createData()).thenReturn(new ConnectionData("vkontakte", "1", null, null, null, null, null, null, null));
        when(vkontakte.usersOperations()).thenReturn(usersOperations);
        when(vkApiClient.photos()).thenReturn(photos);
        when(connection.fetchUserProfile()).thenReturn(new UserProfile(null, null, null, null, "mail@mail.com", null));
        vKontakteDataProvider = new VKontakteDataProvider(connection) {
            @Override
            protected VkApiClient createVpApiClient() {
                return vkApiClient;
            }
        };
    }

    @Test
    public void getAccountId() throws Exception {
        assertEquals("1", vKontakteDataProvider.getAccountId());
    }

    @Test
    public void enhanceUser() throws Exception {
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setId(10L);
        vKontakteProfile.setFirstName("firstName");
        vKontakteProfile.setGender("W");
        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 1990));
        when(usersOperations.getUser("sex, bdate")).thenReturn(vKontakteProfile);

        List<com.vk.api.sdk.objects.photos.Photo> vkPhotos = Arrays.asList(mockVkPhoto(10, "src"));
        PhotosGetQuery query = mock(PhotosGetQuery.class);
        GetResponse response = mock(GetResponse.class);
        when(query.execute()).thenReturn(response);
        when(query.albumId(any(String.class))).thenReturn(query);
        when(query.count(any(Integer.class))).thenReturn(query);
        when(query.rev(true)).thenReturn(query);
        when(photos.get(any(UserActor.class))).thenReturn(query);
        when(response.getItems()).thenReturn(vkPhotos);

        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals("firstName", user.getName());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1990-02-01"), user.getDateOfBirth());
        assertEquals(Gender.FEMALE, user.getGender());
        assertEquals("mail@mail.com", user.getEmail());
        Photo photo = user.getPhotos().get(0);
        assertEquals("10", photo.getIdOnAccount());
        assertEquals("src", photo.getSourceLink());
        assertEquals(UserAccount.Type.VKONTAKTE, photo.getAccountType());
        assertEquals(user, photo.getUser());
    }

    @Test
    public void enhanceUser_sex() throws Exception {
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setId(10L);
        vKontakteProfile.setGender("W");
        when(usersOperations.getUser("sex, bdate")).thenReturn(vKontakteProfile);

        List<com.vk.api.sdk.objects.photos.Photo> vkPhotos = Arrays.asList(mockVkPhoto(10, "src"));
        PhotosGetQuery query = mock(PhotosGetQuery.class);
        GetResponse response = mock(GetResponse.class);
        when(query.execute()).thenReturn(response);
        when(query.albumId(any(String.class))).thenReturn(query);
        when(query.count(any(Integer.class))).thenReturn(query);
        when(query.rev(true)).thenReturn(query);
        when(photos.get(any(UserActor.class))).thenReturn(query);
        when(response.getItems()).thenReturn(vkPhotos);

        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(Gender.FEMALE, user.getGender());

        vKontakteProfile.setGender("M");
        user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(Gender.MALE, user.getGender());

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
        vKontakteProfile.setId(10L);
        vKontakteProfile.setGender("W");
        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 1990));
        when(usersOperations.getUser("sex, bdate")).thenReturn(vKontakteProfile);

        List<com.vk.api.sdk.objects.photos.Photo> vkPhotos = Arrays.asList(mockVkPhoto(10, "src"));
        PhotosGetQuery query = mock(PhotosGetQuery.class);
        GetResponse response = mock(GetResponse.class);
        when(query.execute()).thenReturn(response);
        when(query.albumId(any(String.class))).thenReturn(query);
        when(query.count(any(Integer.class))).thenReturn(query);
        when(query.rev(true)).thenReturn(query);
        when(photos.get(any(UserActor.class))).thenReturn(query);
        when(response.getItems()).thenReturn(vkPhotos);

        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1990-02-01"), user.getDateOfBirth());

        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 0));
        user = vKontakteDataProvider.enhanceUser(new User());
        assertNull(user.getDateOfBirth());
    }

    @Test
    public void getPhotos() throws Exception {
        PhotosGetAllQuery query = mock(PhotosGetAllQuery.class);
        GetAllResponse response = mock(GetAllResponse.class);
        when(query.execute()).thenReturn(response);
        when(query.count(20)).thenReturn(query);
        when(photos.getAll(any(UserActor.class))).thenReturn(query);

        List<com.vk.api.sdk.objects.photos.PhotoXtrRealOffset> vkPhotos = Arrays.asList(
                mockVkPhotoXtrRealOffset(1, "src1"),
                mockVkPhotoXtrRealOffset(2, "src2")
        );
        when(response.getItems()).thenReturn(vkPhotos);
        List<Photo> photos = vKontakteDataProvider.getPhotos();
        int i = 1;
        for(Photo photo : photos) {
            assertEquals(i + "", photo.getIdOnAccount());
            assertEquals("src" + i, photo.getSourceLink());
            assertEquals(UserAccount.Type.VKONTAKTE, photo.getAccountType());
            i++;
        }
    }

    @Test
    public void getPhoto() throws Exception {
        List<com.vk.api.sdk.objects.photos.Photo> vkPhotos = new ArrayList<>();
        vkPhotos.add(mockVkPhoto(1, "src1"));
        PhotosGetByIdQuery query = mock(PhotosGetByIdQuery.class);
        when(query.execute()).thenReturn(vkPhotos);
        when(photos.getById("1_1")).thenReturn(query);
        Optional<Photo> photoOptional = vKontakteDataProvider.getPhoto("1");
        assertTrue(photoOptional.isPresent());
        Photo photo = photoOptional.get();
        assertEquals("1", photo.getIdOnAccount());
        assertEquals("src1", photo.getSourceLink());
        assertEquals(UserAccount.Type.VKONTAKTE, photo.getAccountType());
    }

    @Test
    public void getPhoto_whenNotFound_emptyOptional() throws Exception {
        PhotosGetByIdQuery query = mock(PhotosGetByIdQuery.class);
        when(query.execute()).thenReturn(new ArrayList<>());
        when(photos.getById("1_1")).thenReturn(query);
        Optional<Photo> photoOptional = vKontakteDataProvider.getPhoto("1");
        assertFalse(photoOptional.isPresent());
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

}