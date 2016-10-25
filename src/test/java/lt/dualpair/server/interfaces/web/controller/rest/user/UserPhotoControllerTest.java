package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import lt.dualpair.server.service.user.SocialDataProviderFactory;
import lt.dualpair.server.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserPhotoControllerTest {

    private UserPhotoController userPhotoController = new UserPhotoController();
    private PhotoResourceAssembler photoResourceAssembler = mock(PhotoResourceAssembler.class);
    private SocialDataProviderFactory socialDataProviderFactory = mock(SocialDataProviderFactory.class);
    private UserService userService = mock(UserService.class);

    @Before
    public void setUp() throws Exception {
        userPhotoController.setPhotoResourceAssembler(photoResourceAssembler);
        userPhotoController.setSocialDataProviderFactory(socialDataProviderFactory);
        userPhotoController.setUserService(userService);
    }

    @Test
    public void testDeletePhoto_forbidden() throws Exception {
        ResponseEntity responseEntity = userPhotoController.deletePhoto(2L, 1L, UserTestUtils.createUser());
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testDeletePhoto() throws Exception {
        ResponseEntity responseEntity = userPhotoController.deletePhoto(1L, 1L, UserTestUtils.createUser());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userService, times(1)).deleteUserPhoto(1L, 1L);
    }
}