package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PhotoResourceAssemblerTest {

    private PhotoResourceAssembler photoResourceAssembler = new PhotoResourceAssembler();

    @Test
    public void toResource() throws Exception {
        Photo photo = new Photo();
        photo.setId(1L);
        photo.setUser(new User());
        photo.setSourceLink("url");
        photo.setPosition(5);
        PhotoResource photoResource = photoResourceAssembler.toResource(photo);
        assertEquals((Long)1L, photoResource.getPhotoId());
        assertTrue(photoResource.getSource().endsWith("/photo?name=url"));
        assertEquals((Integer)5, photoResource.getPosition());
    }

    @Test
    public void toResource_whenSourceIsAbsoluteLink_itIsNotConverted() {
        Photo photo = new Photo();
        photo.setSourceLink("httpsomelink");
        PhotoResource photoResource = photoResourceAssembler.toResource(photo);
        assertEquals("httpsomelink", photoResource.getSource());
    }
}