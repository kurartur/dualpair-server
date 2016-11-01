package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.user.PhotoResource;
import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import lt.dualpair.server.service.user.SocialDataProvider;
import lt.dualpair.server.service.user.SocialDataProviderFactory;
import lt.dualpair.server.service.user.SocialUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserPhotoControllerTest {

    private UserPhotoController userPhotoController = new UserPhotoController();
    private PhotoResourceAssembler photoResourceAssembler = mock(PhotoResourceAssembler.class);
    private SocialDataProviderFactory socialDataProviderFactory = mock(SocialDataProviderFactory.class);
    private SocialUserService socialUserService = mock(SocialUserService.class);

    @Before
    public void setUp() throws Exception {
        userPhotoController.setPhotoResourceAssembler(photoResourceAssembler);
        userPhotoController.setSocialDataProviderFactory(socialDataProviderFactory);
        userPhotoController.setSocialUserService(socialUserService);
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
        verify(socialUserService, times(1)).deleteUserPhoto(1L, 1L);
    }

    @Test
    public void testGetAvailablePhotos_forbidden() throws Exception {
        ResponseEntity responseEntity = userPhotoController.getAvailablePhotos(2L, null, UserTestUtils.createUser());
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testGetAvailablePhotos() throws Exception {
        List<Photo> photos = Arrays.asList(new Photo());
        SocialDataProvider socialDataProvider = mock(SocialDataProvider.class);
        when(socialDataProvider.getPhotos()).thenReturn(photos);
        PhotoResource photoResource = new PhotoResource();
        List<PhotoResource> photoResources = Arrays.asList(photoResource);
        when(photoResourceAssembler.toResources(photos)).thenReturn(photoResources);
        when(socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, "username")).thenReturn(socialDataProvider);
        ResponseEntity responseEntity = userPhotoController.getAvailablePhotos(1L, "FB", UserTestUtils.createUser());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(photoResources, responseEntity.getBody());
    }

    @Test
    public void testAddPhoto_forbidden() throws Exception {
        ResponseEntity responseEntity = userPhotoController.addPhoto(2L, null, UserTestUtils.createUser());
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testAddPhoto() throws Exception {
        PhotoResource photoResource = new PhotoResource();
        photoResource.setAccountType("FB");
        photoResource.setIdOnAccount("idOnAccount");
        photoResource.setSourceUrl("url");
        Photo photo = new Photo();
        when(socialUserService.addUserPhoto(eq(1L), any(Photo.class))).thenReturn(photo);
        PhotoResource newPhotoResource = new PhotoResource();
        when(photoResourceAssembler.toResource(photo)).thenReturn(newPhotoResource);
        ResponseEntity responseEntity = userPhotoController.addPhoto(1L, photoResource, UserTestUtils.createUser());
        ArgumentCaptor<Photo> argumentCaptor = ArgumentCaptor.forClass(Photo.class);
        verify(socialUserService, times(1)).addUserPhoto(eq(1L), argumentCaptor.capture());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(newPhotoResource, responseEntity.getBody());
        Photo capturedPhoto = argumentCaptor.getValue();
        assertEquals(UserAccount.Type.FACEBOOK, capturedPhoto.getAccountType());
        assertEquals("idOnAccount", capturedPhoto.getIdOnAccount());
        assertEquals("url", capturedPhoto.getSourceLink());
    }
}