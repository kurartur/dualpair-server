package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.persistence.repository.PhotoRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;

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
    }

    @Test
    public void testAddUserPhoto_photoDoesntExistOnSocialAccount() throws Exception {
        prepareAddUserPhoto(false);
        try {
            socialUserService.addUserPhoto(1L, UserAccount.Type.FACEBOOK, "idOnAccount", 5);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Photo doesn't exist on account or is not public", iae.getMessage());
        }
    }

    @Test
    public void testAddUserPhoto() throws Exception {
        prepareAddUserPhoto(true);
        Photo photo =socialUserService.addUserPhoto(1L, UserAccount.Type.FACEBOOK, "idOnAccount", 5);
        assertEquals("username", photo.getUser().getUsername());
        assertEquals(5, photo.getPosition());
        assertEquals("idOnAccount", photo.getIdOnAccount());
        assertEquals("url", photo.getSourceLink());
        verify(photoRepository, times(1)).save(photo);
    }

    private void prepareAddUserPhoto(boolean photoExists) {
        User user = new User();
        user.setUsername("username");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        SocialDataProvider socialDataProvider = mock(SocialDataProvider.class);
        when(socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, "username")).thenReturn(socialDataProvider);
        if (photoExists) {
            Photo photo = new Photo();
            photo.setIdOnAccount("idOnAccount");
            photo.setAccountType(UserAccount.Type.FACEBOOK);
            photo.setSourceLink("url");
            when(socialDataProvider.getPhoto("idOnAccount")).thenReturn(Optional.of(photo));
        } else {
            when(socialDataProvider.getPhoto("idOnAccount")).thenReturn(Optional.empty());
        }
    }

}