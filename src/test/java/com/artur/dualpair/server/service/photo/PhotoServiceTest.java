package com.artur.dualpair.server.service.photo;

import com.artur.dualpair.server.domain.model.photo.Photo;
import com.artur.dualpair.server.persistence.repository.PhotoRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class PhotoServiceTest {

    private PhotoService photoService = new PhotoService();
    private PhotoRepository photoRepository = mock(PhotoRepository.class);

    @Before
    public void setUp() throws Exception {
        photoService.setPhotoRepository(photoRepository);
    }

    @Test
    public void testGetUserPhoto() throws Exception {
        Photo photo = new Photo();
        doReturn(Optional.of(photo)).when(photoRepository).findUserPhoto(1L, 1L);
        assertEquals(photo, photoService.getUserPhoto(1L, 1L));
    }

    @Test
    public void testGetUserPhoto_notFound() throws Exception {
        doReturn(Optional.empty()).when(photoRepository).findUserPhoto(1L, 1L);
        assertNull(photoService.getUserPhoto(1L, 1L));
    }
}
