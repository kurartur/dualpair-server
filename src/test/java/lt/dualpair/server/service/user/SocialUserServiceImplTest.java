package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.photo.PhotoTestUtils;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.persistence.repository.PhotoRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SocialUserServiceImplTest {

    private SocialUserServiceImpl socialUserService = new SocialUserServiceImpl();
    private UserRepository userRepository = mock(UserRepository.class);
    private SocialDataProviderFactory socialDataProviderFactory = mock(SocialDataProviderFactory.class);
    private PhotoRepository photoRepository = mock(PhotoRepository.class);

    @Before
    public void setUp() throws Exception {
        socialUserService.setUserRepository(userRepository);
        socialUserService.setSocialDataProviderFactory(socialDataProviderFactory);
        socialUserService.setPhotoRepository(photoRepository);

        User user = new User();
        user.setUsername("username");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        SocialDataProvider socialDataProvider = mock(SocialDataProvider.class);
        when(socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, "username")).thenReturn(socialDataProvider);
        Photo photo1 = PhotoTestUtils.createPhoto(UserAccount.Type.FACEBOOK, "idOnAccount1", "url1");
        Photo photo2 = PhotoTestUtils.createPhoto(UserAccount.Type.FACEBOOK, "idOnAccount2", "url2");
        Photo photo3 = PhotoTestUtils.createPhoto(UserAccount.Type.FACEBOOK, "idOnAccount3", "url3");
        when(socialDataProvider.getPhoto("idOnAccount1")).thenReturn(Optional.of(photo1));
        when(socialDataProvider.getPhoto("idOnAccount2")).thenReturn(Optional.of(photo2));
        when(socialDataProvider.getPhoto("idOnAccount3")).thenReturn(Optional.of(photo3));
        when(socialDataProvider.getPhoto("idOnAccount4")).thenReturn(Optional.empty());
        when(socialDataProvider.getPhotos(any(List.class))).thenAnswer(new Answer<List>() {
            @Override
            public List answer(InvocationOnMock invocation) throws Throwable {
                List ids = (List)invocation.getArguments()[0];
                List<Photo> result = new ArrayList<>();
                if (ids.contains("idOnAccount1")) result.add(photo1);
                if (ids.contains("idOnAccount2")) result.add(photo2);
                if (ids.contains("idOnAccount3")) result.add(photo3);
                return result;
            }
        });
    }

    @Test
    public void testAddUserPhoto_whenPhotoDoesntExistOnSocialAccount_exceptionThrown() throws Exception {
        try {
            socialUserService.addUserPhoto(1L, UserAccount.Type.FACEBOOK, "idOnAccount4", 5);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Photo doesn't exist on account or is not public", iae.getMessage());
        }
    }

    @Test
    public void testAddUserPhoto() throws Exception {
        Photo photo = socialUserService.addUserPhoto(1L, UserAccount.Type.FACEBOOK, "idOnAccount1", 5);
        assertEquals("username", photo.getUser().getUsername());
        assertEquals(5, photo.getPosition());
        assertEquals("idOnAccount1", photo.getIdOnAccount());
        assertEquals("url1", photo.getSourceLink());
        verify(photoRepository, times(1)).save(photo);
    }

    @Test
    public void testSetUserPhotos_whenPhotoDoesntExistOnSocialAccount_exceptionThrown() throws Exception {
        List<SocialUserService.PhotoData> photoDataList = new ArrayList<>();
        photoDataList.add(createPhotoData(UserAccount.Type.FACEBOOK, "idOnAccount1", 0));
        photoDataList.add(createPhotoData(UserAccount.Type.FACEBOOK, "idOnAccount4", 1));
        try {
            socialUserService.setUserPhotos(1L, photoDataList);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Photo(s) [idOnAccount4] do(es)n't exist on account or is (are) not public", iae.getMessage());
        }
    }

    @Test
    public void testSetUserPhotos() throws Exception {
        List<SocialUserService.PhotoData> photoDataList = new ArrayList<>();
        photoDataList.add(createPhotoData(UserAccount.Type.FACEBOOK, "idOnAccount1", 0));
        photoDataList.add(createPhotoData(UserAccount.Type.FACEBOOK, "idOnAccount2", 1));
        List<Photo> returnedPhotos = socialUserService.setUserPhotos(1L, photoDataList);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User user = userCaptor.getValue();
        List<Photo> userPhotos = user.getPhotos();
        assertEquals(2, userPhotos.size());
        assertEquals(returnedPhotos, userPhotos);
    }

    private SocialUserService.PhotoData createPhotoData(UserAccount.Type accountType, String idOnAccount, int position) {
        SocialUserService.PhotoData photoData = new SocialUserService.PhotoData();
        photoData.accountType = accountType;
        photoData.idOnAccount = idOnAccount;
        photoData.position = position;
        return photoData;
    }

}