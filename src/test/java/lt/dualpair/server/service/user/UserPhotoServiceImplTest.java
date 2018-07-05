package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.photo.PhotoRepository;
import lt.dualpair.core.photo.PhotoTestUtils;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UserPhotoServiceImplTest {

    private UserPhotoServiceImpl service;
    private PhotoRepository photoRepository = mock(PhotoRepository.class);
    private SocialDataProviderFactory socialDataProviderFactory = mock(SocialDataProviderFactory.class);
    private UserService userService = mock(UserService.class);

    @Before
    public void setUp() throws Exception {
        String tmpDir = System.getProperty("java.io.tmpdir");
        service = new UserPhotoServiceImpl(photoRepository, socialDataProviderFactory, userService, tmpDir);

        User user = new User();
        user.setId(1L);
        when(userService.loadUserById(1L)).thenReturn(user);

        SocialDataProvider socialDataProvider = mock(SocialDataProvider.class);
        when(socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, 1L)).thenReturn(socialDataProvider);
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
    public void testAddPhoto_whenPhotoDoesntExistOnSocialAccount_exceptionThrown() throws Exception {
        try {
            service.addPhoto(1L, UserAccount.Type.FACEBOOK, "idOnAccount4", 5);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Photo doesn't exist on account or is not public", iae.getMessage());
        }
    }

    @Test
    public void testAddPhoto() throws Exception {
        Photo photo = service.addPhoto(1L, UserAccount.Type.FACEBOOK, "idOnAccount1", 5);
        assertEquals((Long)1L, photo.getUser().getId());
        assertEquals(5, photo.getPosition());
        assertEquals("idOnAccount1", photo.getIdOnAccount());
        assertEquals("url1", photo.getSourceLink());
        verify(photoRepository, times(1)).save(photo);
    }
}