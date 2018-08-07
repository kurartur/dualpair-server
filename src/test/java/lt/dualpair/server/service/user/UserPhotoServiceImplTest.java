package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class UserPhotoServiceImplTest {

    private UserPhotoServiceImpl service;
    private UserRepository userRepository = mock(UserRepository.class);
    private PhotoFileHelper photoStore = mock(PhotoFileHelper.class);
    private ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    @Before
    public void setUp() throws Exception {
        service = new UserPhotoServiceImpl(userRepository, photoStore);
        List<Photo> photos = new ArrayList<>();
        photos.add(createPhoto(1L, "s1"));
        photos.add(createPhoto(2L, "s2"));
        photos.add(createPhoto(3L, "s2"));
        User user = new User();
        user.setId(1L);
        user.setPhotos(photos);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    public void setUserPhotos_containsAtLeastOnePhoto() {
        try {
            service.setUserPhotos(1L, new ArrayList<>());
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Must have at least one photo", iae.getMessage());
        }
    }

    @Test
    public void setUserPhoto_photosAreDeleted() {
        List<Photo> result = service.setUserPhotos(1L, Arrays.asList(new PhotoModel(1L, null, 3)));
        verify(userRepository, times(1)).save(userCaptor.capture());
        List<Photo> savedPhotos = userCaptor.getValue().getPhotos();
        assertEquals(1, savedPhotos.size());
        assertEquals(new Long(1L), savedPhotos.get(0).getId());
        assertEquals(savedPhotos, result);
    }

    @Test
    public void setUserPhoto_photosAreUpdated() {
        List<Photo> result = service.setUserPhotos(1L, Arrays.asList(new PhotoModel(1L, null, 3)));
        verify(userRepository, times(1)).save(userCaptor.capture());
        List<Photo> savedPhotos = userCaptor.getValue().getPhotos();
        assertEquals(1, savedPhotos.size());
        assertEquals(new Long(1L), savedPhotos.get(0).getId());
        assertEquals(3, savedPhotos.get(0).getPosition());
        assertEquals(savedPhotos, result);
    }

    @Test
    public void setUserPhoto_photosAreAdded() {
        byte[] photoBytes = new byte[10];
        when(photoStore.save(photoBytes, 1L, 2)).thenReturn("path");
        List<PhotoModel> photoList = Arrays.asList(new PhotoModel(1L, null, 3), new PhotoModel(null, photoBytes, 2));
        List<Photo> result = service.setUserPhotos(1L, photoList);
        verify(userRepository, times(1)).save(userCaptor.capture());
        List<Photo> savedPhotos = userCaptor.getValue().getPhotos();
        assertEquals(2, savedPhotos.size());
        Photo firstPhoto = savedPhotos.get(0);
        assertEquals(new Long(1L), firstPhoto.getId());
        assertEquals(3, firstPhoto.getPosition());
        Photo secondPhoto = savedPhotos.get(1);
        assertEquals(2, secondPhoto.getPosition());
        assertEquals("path", secondPhoto.getSourceLink());
        assertEquals(savedPhotos, result);
    }

    @Test
    public void setUserPhoto_photosAreUpdatedAndAdded() {
        byte[] photoBytes1 = new byte[10];
        byte[] photoBytes2 = new byte[10];
        when(photoStore.save(photoBytes1, 1L, 2)).thenReturn("path1");
        when(photoStore.save(photoBytes2, 1L, 3)).thenReturn("path2");
        List<PhotoModel> photoList = Arrays.asList(new PhotoModel(1L, null, 1), new PhotoModel(null, photoBytes1, 2), new PhotoModel(null, photoBytes2, 3));
        List<Photo> result = service.setUserPhotos(1L, photoList);
        verify(userRepository, times(1)).save(userCaptor.capture());
        List<Photo> savedPhotos = userCaptor.getValue().getPhotos();
        assertEquals(3, savedPhotos.size());
        assertEquals(3, result.size());
        assertEquals(savedPhotos, result);
    }

    private Photo createPhoto(Long id, String source) {
        Photo photo = new Photo();
        photo.setId(id);
        photo.setSourceLink(source);
        return photo;
    }
}