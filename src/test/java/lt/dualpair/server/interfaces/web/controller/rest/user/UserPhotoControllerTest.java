package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.UserAccount;
import lt.dualpair.server.interfaces.resource.user.PhotoResource;
import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import lt.dualpair.server.security.TestUserDetails;
import lt.dualpair.server.service.user.SocialDataProvider;
import lt.dualpair.server.service.user.SocialDataProviderFactory;
import lt.dualpair.server.service.user.SocialUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserPhotoControllerTest {

    private UserPhotoController userPhotoController = new UserPhotoController();
    private PhotoResourceAssembler photoResourceAssembler = mock(PhotoResourceAssembler.class);
    private SocialDataProviderFactory socialDataProviderFactory = mock(SocialDataProviderFactory.class);
    private SocialUserService socialUserService = mock(SocialUserService.class);
    private SocialDataProvider socialDataProvider = mock(SocialDataProvider.class);

    @Captor
    private ArgumentCaptor<List<SocialUserService.PhotoData>> photoDataCaptor;

    @Before
    public void setUp() throws Exception {
        userPhotoController.setPhotoResourceAssembler(photoResourceAssembler);
        userPhotoController.setSocialDataProviderFactory(socialDataProviderFactory);
        userPhotoController.setSocialUserService(socialUserService);
        when(socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, 1L)).thenReturn(socialDataProvider);
    }

    @Test
    public void testDeletePhoto_forbidden() throws Exception {
        ResponseEntity responseEntity = userPhotoController.deletePhoto(2L, 1L, new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testDeletePhoto() throws Exception {
        ResponseEntity responseEntity = userPhotoController.deletePhoto(1L, 1L, new TestUserDetails(1L));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(socialUserService, times(1)).deleteUserPhoto(1L, 1L);
    }

    @Test
    public void testGetAvailablePhotos_forbidden() throws Exception {
        ResponseEntity responseEntity = userPhotoController.getAvailablePhotos(2L, null, new TestUserDetails(1L));
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
        when(socialDataProviderFactory.getProvider(UserAccount.Type.FACEBOOK, 1L)).thenReturn(socialDataProvider);
        ResponseEntity responseEntity = userPhotoController.getAvailablePhotos(1L, "FB", new TestUserDetails(1L));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(photoResources, responseEntity.getBody());
    }

    @Test
    public void testAddPhoto_forbidden() throws Exception {
        ResponseEntity responseEntity = userPhotoController.addPhoto(2L, null, new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testAddPhoto() throws Exception {
        PhotoResource photoResource = new PhotoResource();
        photoResource.setAccountType("FB");
        photoResource.setIdOnAccount("idOnAccount");
        photoResource.setPosition(5);

        Photo photo = new Photo();

        when(socialUserService.addUserPhoto(1L, UserAccount.Type.FACEBOOK, "idOnAccount", 5)).thenReturn(photo);

        PhotoResource newPhotoResource = new PhotoResource();
        when(photoResourceAssembler.toResource(photo)).thenReturn(newPhotoResource);

        ResponseEntity responseEntity = userPhotoController.addPhoto(1L, photoResource, new TestUserDetails(1L));
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(newPhotoResource, responseEntity.getBody());
    }

    @Test
    public void testSetPhotos_forbidden() throws Exception {
        ResponseEntity responseEntity = userPhotoController.setPhotos(2L, new ArrayList<>(), new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testSetPhotos() throws Exception {
        List<PhotoResource> photoResourceList = new ArrayList<>();
        PhotoResource photoResource = new PhotoResource();
        photoResource.setAccountType("FB");
        photoResource.setIdOnAccount("idOnAccount");
        photoResource.setPosition(1);
        photoResourceList.add(photoResource);

        ResponseEntity responseEntity = userPhotoController.setPhotos(1L, photoResourceList, new TestUserDetails(1L));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(socialUserService, times(1)).setUserPhotos(eq(1L), photoDataCaptor.capture());
        List<SocialUserService.PhotoData> photoDataList = photoDataCaptor.getValue();
        assertEquals(1, photoDataList.size());
        SocialUserService.PhotoData photoData = photoDataList.get(0);
        assertEquals(UserAccount.Type.FACEBOOK, photoData.accountType);
        assertEquals("idOnAccount", photoData.idOnAccount);
        assertEquals(1, photoData.position);
    }
}