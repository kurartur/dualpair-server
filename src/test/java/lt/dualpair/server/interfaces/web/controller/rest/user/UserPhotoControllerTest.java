package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import lt.dualpair.server.service.user.PhotoFileHelper;
import lt.dualpair.server.service.user.PhotoModel;
import lt.dualpair.server.service.user.UserPhotoService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class UserPhotoControllerTest {

    private UserPhotoController controller;
    private PhotoResourceAssembler photoResourceAssembler = mock(PhotoResourceAssembler.class);
    private UserPhotoService userPhotoService = mock(UserPhotoService.class);
    private PhotoFileHelper photoFileHelper = mock(PhotoFileHelper.class);
    private ArgumentCaptor<List> photoModelListCaptor = ArgumentCaptor.forClass(List.class);

    @Before
    public void setUp() throws Exception {
        controller = new UserPhotoController(photoResourceAssembler, userPhotoService, photoFileHelper);
    }

    @Test
    public void testSetPhotos_oneOldOneNew() throws Exception {
        List<MultipartFile> files = new ArrayList<>();
        byte[] bytes = new byte[10];
        MultipartFile file = new MockMultipartFile("photoFiles", "1", null, bytes);
        files.add(file);
        ResponseEntity responseEntity = controller.setPhotos(1L, files, "{\"photoResources\" : [{\"source\" : \"1\", \"position\" : \"1\"}, {\"id\":\"1\", \"position\":\"2\"}]}");
        verify(userPhotoService, times(1)).setUserPhotos(eq(1L), photoModelListCaptor.capture());
        PhotoModel model1 = (PhotoModel) photoModelListCaptor.getValue().get(0);
        assertEquals(bytes, model1.getPhoto());
        assertEquals(1, model1.getPosition());
        assertNull(model1.getId());
        PhotoModel model2 = (PhotoModel) photoModelListCaptor.getValue().get(1);
        assertNull(model2.getPhoto());
        assertEquals(2, model2.getPosition());
        assertEquals(new Long(1L), model2.getId());
    }

    @Test
    public void testSetPhotos_whenCountInDataDoesNotEqualModelCount_exceptionThrown() throws Exception {
        List<MultipartFile> files = new ArrayList<>();
        byte[] bytes = new byte[10];
        MultipartFile file = new MockMultipartFile("photoFiles", "2", null, bytes);
        files.add(file);
        try {
            controller.setPhotos(1L, files, "{\"photoResources\" : [{\"source\" : \"1\", \"position\" : \"1\"}, {\"id\":\"1\", \"position\":\"2\"}]}");
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Model count is not equal to photo count in received data", iae.getMessage());
        }
    }
}