package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.photo.Photo;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.service.photo.PhotoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhotoControllerTest {

    private PhotoController photoController = new PhotoController();
    private PhotoService photoService = mock(PhotoService.class);

    @Before
    public void setUp() throws Exception {
        photoController.setPhotoService(photoService);
        User principal = new User();
        principal.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null));
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testPhoto() throws Exception {
        Photo photo = new Photo();
        photo.setSourceLink("http://photo1");
        when(photoService.getUserPhoto(1L, 1L)).thenReturn(photo);
        ResponseEntity response = photoController.photo(1L);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals(new URI("http://photo1"), response.getHeaders().getLocation());
    }

    @Test
    public void testPhoto_notFound() throws Exception {
        ResponseEntity response = photoController.photo(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}