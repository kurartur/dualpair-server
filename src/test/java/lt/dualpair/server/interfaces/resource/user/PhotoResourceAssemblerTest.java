package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.UserAccount;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PhotoResourceAssemblerTest {

    private PhotoResourceAssembler photoResourceAssembler = new PhotoResourceAssembler();

    @Test
    public void toResource() throws Exception {
        Photo photo = new Photo();
        photo.setAccountType(UserAccount.Type.FACEBOOK);
        photo.setIdOnAccount("idOnAccount");
        photo.setId(1L);
        photo.setSourceLink("url");
        photo.setPosition(5);
        PhotoResource photoResource = photoResourceAssembler.toResource(photo);
        assertEquals("FB", photoResource.getAccountType());
        assertEquals("idOnAccount", photoResource.getIdOnAccount());
        assertEquals((Long)1L, photoResource.getPhotoId());
        assertEquals("url", photoResource.getSourceUrl());
        assertEquals((Integer)5, photoResource.getPosition());
    }

}