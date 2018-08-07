package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.*;
import org.springframework.web.client.RestOperations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class FacebookDataProviderTest {

    private FacebookDataProvider facebookDataProvider;
    private Connection<Facebook> facebookConnection = mock(Connection.class);
    private Facebook facebook = mock(Facebook.class);
    private UserOperations userOperations = mock(UserOperations.class);
    private MediaOperations mediaOperations = mock(MediaOperations.class);
    private RestOperations restOperations = mock(RestOperations.class);
    private org.springframework.social.facebook.api.User facebookUser = mock(org.springframework.social.facebook.api.User.class);
    private UserProfile userProfile = new UserProfile("id", "name", "firstName", "lastName", "email", "username");

    @Before
    public void setUp() throws Exception {
        facebookDataProvider = new FacebookDataProvider(facebookConnection);
        when(facebookConnection.getApi()).thenReturn(facebook);
        when(facebookConnection.fetchUserProfile()).thenReturn(userProfile);
        when(facebook.userOperations()).thenReturn(userOperations);
        when(facebook.mediaOperations()).thenReturn(mediaOperations);
        when(facebook.restOperations()).thenReturn(restOperations);
        when(userOperations.getUserProfile()).thenReturn(facebookUser);

        when(facebookUser.getGender()).thenReturn("male"); // default gender
    }

    @Test
    public void testEnhanceUser() throws Exception {
        when(facebookUser.getFirstName()).thenReturn("firstName");
        LocalDate dateOfBirth = createFacebookUserDateOfBirth(false);
        when(facebookUser.getBirthday()).thenReturn(dateOfBirthToString(dateOfBirth, false));
        when(facebookUser.getGender()).thenReturn("male");
        doReturn(new PagedList<>(Arrays.asList(createAlbum("1", "Profile pictures"), createAlbum("2", "Cool pics")), null, null)).when(mediaOperations).getAlbums();
        doReturn(new PagedList<>(Arrays.asList(createPhoto("1"), createPhoto("2")), null, null)).when(mediaOperations).getPhotos("1");
        doReturn(new PagedList<>(Arrays.asList(createPhoto("3"), createPhoto("4")), null, null)).when(mediaOperations).getPhotos("2");

        User user = new User();
        user = facebookDataProvider.enhanceUser(user);
        assertEquals("firstName", user.getName());
        assertEquals("email", user.getEmail());
        assertEquals((Integer) 5, user.getAge());
        assertEquals(Date.from(dateOfBirth.atStartOfDay(ZoneId.systemDefault()).toInstant()), user.getDateOfBirth());
        assertEquals(Gender.MALE, user.getGender());
        assertEquals(2, user.getPhotos().size());
        Iterator<Photo> photos = user.getPhotos().iterator();
        assertUserPhoto("1", user, photos.next());
        assertUserPhoto("2", user, photos.next());
    }

    @Test
    public void testEnhanceUser_genderMale() throws Exception {
        when(facebookUser.getGender()).thenReturn("male");
        User user = new User();
        user = facebookDataProvider.enhanceUser(user);
        assertEquals(Gender.MALE, user.getGender());
    }

    @Test
    public void testEnhanceUser_genderFemale() throws Exception {
        when(facebookUser.getGender()).thenReturn("female");
        User user = new User();
        user = facebookDataProvider.enhanceUser(user);
        assertEquals(Gender.FEMALE, user.getGender());
    }

    @Test
    public void testEnhanceUser_invalidGender() throws Exception {
        when(facebookUser.getGender()).thenReturn("other");
        User user = new User();
        try {
            facebookDataProvider.enhanceUser(user);
            fail();
        } catch (SocialDataException sde) {
            assertEquals("Invalid gender 'other'", sde.getMessage());
        }
    }

    @Test
    public void testEnhanceUser_dateYearOnly() throws Exception {
        when(facebookUser.getGender()).thenReturn("male");
        LocalDate dateOfBirth = createFacebookUserDateOfBirth(true);
        when(facebookUser.getBirthday()).thenReturn(dateOfBirthToString(dateOfBirth, true));

        User user = new User();
        facebookDataProvider.enhanceUser(user);
        assertEquals((Integer) 5, user.getAge());
    }

    @Test
    public void testEnhanceUser_photoLimit() throws Exception {
        doReturn(new PagedList<>(Arrays.asList(createAlbum("1", "Profile pictures")), null, null)).when(mediaOperations).getAlbums();
        List<org.springframework.social.facebook.api.Photo> fbPhotos = new ArrayList<>();
        for (int i = 0 ; i < User.MAX_NUMBER_OF_PHOTOS + 5; i++) {
            fbPhotos.add(createPhoto(i + ""));
        }
        doReturn(new PagedList<>(fbPhotos, null, null)).when(mediaOperations).getPhotos("1");

        User user = new User();
        user = facebookDataProvider.enhanceUser(user);

        assertEquals(User.MAX_NUMBER_OF_PHOTOS, user.getPhotos().size());
    }

    private void assertUserPhoto(String expectedId, User expectedUser, Photo actualPhoto) throws Exception {
        assertEquals("http://photo" + expectedId, actualPhoto.getSourceLink());
        assertEquals(expectedUser, actualPhoto.getUser());
    }

    private LocalDate createFacebookUserDateOfBirth(boolean yearOnly) {
        LocalDate date = LocalDate.now().minus(5, ChronoUnit.YEARS);
        if (yearOnly) {
            date.withMonth(1).withDayOfMonth(1);
        } else {
            date.minus(1, ChronoUnit.DAYS);
        }
        return date;
    }

    private String dateOfBirthToString(LocalDate dateOfBirth, boolean yearOnly) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(yearOnly ? "yyyy" : "MM/dd/yyyy");
        return dateOfBirth.format(formatter);
    }

    private Album createAlbum(String id, String name) {
        Album album = mock(Album.class);
        when(album.getId()).thenReturn(id);
        when(album.getName()).thenReturn(name);
        return album;
    }

    private org.springframework.social.facebook.api.Photo createPhoto(String id) {
        org.springframework.social.facebook.api.Photo photo = mock(org.springframework.social.facebook.api.Photo.class);
        when(photo.getId()).thenReturn(id);
        when(photo.getSource()).thenReturn("http://photo" + id);
        return photo;
    }
}