package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.vkontakte.api.*;
import org.springframework.social.vkontakte.api.impl.json.VKArray;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VKontakteDataProviderTest {

    private VKontakteDataProvider vKontakteDataProvider;
    private Connection<VKontakte> connection = mock(Connection.class);
    private VKontakte vkontakte = mock(VKontakte.class);
    private IUsersOperations usersOperations = mock(IUsersOperations.class);
    private IMediaOperations mediaOperations = mock(IMediaOperations.class);

    @Before
    public void setUp() throws Exception {
        when(connection.getApi()).thenReturn(vkontakte);
        when(vkontakte.usersOperations()).thenReturn(usersOperations);
        when(vkontakte.mediaOperations()).thenReturn(mediaOperations);
        when(connection.fetchUserProfile()).thenReturn(new UserProfile(null, null, null, null, "mail@mail.com", null));
        vKontakteDataProvider = new VKontakteDataProvider(connection);
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
        vKontakteProfile.setId(10L);
        vKontakteProfile.setFirstName("firstName");
        vKontakteProfile.setGender("W");
        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 1990));
        when(usersOperations.getUser("sex, bdate")).thenReturn(vKontakteProfile);
        List<org.springframework.social.vkontakte.api.attachment.Photo> vkPhotos = Arrays.asList(mockVkPhoto(10L, "src"));
        when(mediaOperations.getProfilePhotos(10L, 5)).thenReturn(new VKArray<>(1, vkPhotos));
        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals("firstName", user.getName());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1990-02-01"), user.getDateOfBirth());
        assertEquals(User.Gender.FEMALE, user.getGender());
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
        when(mediaOperations.getProfilePhotos(10L, 5)).thenReturn(new VKArray<>(1, new ArrayList<>()));
        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(User.Gender.FEMALE, user.getGender());

        vKontakteProfile.setGender("M");
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
        vKontakteProfile.setId(10L);
        vKontakteProfile.setGender("W");
        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 1990));
        when(usersOperations.getUser("sex, bdate")).thenReturn(vKontakteProfile);
        when(mediaOperations.getProfilePhotos(10L, 5)).thenReturn(new VKArray<>(1, new ArrayList<>()));
        User user = vKontakteDataProvider.enhanceUser(new User());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1990-02-01"), user.getDateOfBirth());

        vKontakteProfile.setBirthDate(new VKontakteDate(1, 2, 0));
        user = vKontakteDataProvider.enhanceUser(new User());
        assertNull(user.getDateOfBirth());
    }

    @Test
    public void getPhotos() throws Exception {
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setId(10L);
        when(usersOperations.getUser()).thenReturn(vKontakteProfile);
        List<org.springframework.social.vkontakte.api.attachment.Photo> vkPhotos = Arrays.asList(
                mockVkPhoto(1L, "src1"),
                mockVkPhoto(2L, "src2")
        );
        when(mediaOperations.getAll(10L, 20)).thenReturn(new VKArray<>(2, vkPhotos));
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
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setId(10L);
        when(usersOperations.getUser()).thenReturn(vKontakteProfile);
        List<org.springframework.social.vkontakte.api.attachment.Photo> vkPhotos = Arrays.asList(mockVkPhoto(1L, "src1"));
        Map<String, String> map = new HashMap<>();
        map.put("10", "1");
        when(mediaOperations.getById(map)).thenReturn(vkPhotos);
        Optional<Photo> photoOptional = vKontakteDataProvider.getPhoto("1");
        assertTrue(photoOptional.isPresent());
        Photo photo = photoOptional.get();
        assertEquals("1", photo.getIdOnAccount());
        assertEquals("src1", photo.getSourceLink());
        assertEquals(UserAccount.Type.VKONTAKTE, photo.getAccountType());
    }

    @Test
    public void getPhoto_notFound() throws Exception {
        VKontakteProfile vKontakteProfile = new VKontakteProfile();
        vKontakteProfile.setId(10L);
        when(usersOperations.getUser()).thenReturn(vKontakteProfile);
        Map<String, String> map = new HashMap<>();
        map.put("10", "1");
        when(mediaOperations.getById(map)).thenReturn(new ArrayList<>());
        Optional<Photo> photoOptional = vKontakteDataProvider.getPhoto("1");
        assertFalse(photoOptional.isPresent());
    }

    private org.springframework.social.vkontakte.api.attachment.Photo mockVkPhoto(Long id, String src) {
        org.springframework.social.vkontakte.api.attachment.Photo vkPhoto = mock(org.springframework.social.vkontakte.api.attachment.Photo.class);
        when(vkPhoto.getPhoto604()).thenReturn(src);
        when(vkPhoto.getPhotoId()).thenReturn(id);
        return vkPhoto;
    }

}