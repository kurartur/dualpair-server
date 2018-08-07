package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class UserPhotoControllerTest {

    private UserPhotoController userPhotoController;
    private PhotoResourceAssembler photoResourceAssembler = mock(PhotoResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSetPhotos() throws Exception {
        // TODO
    }
}