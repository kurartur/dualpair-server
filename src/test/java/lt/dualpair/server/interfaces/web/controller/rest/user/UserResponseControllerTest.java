package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.Response;
import lt.dualpair.server.security.TestUserDetails;
import lt.dualpair.server.service.user.UserResponseService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UserResponseControllerTest {

    private UserResponseController controller;
    private UserResponseService userResponseService = mock(UserResponseService.class);

    @Before
    public void setUp() throws Exception {
        controller = new UserResponseController(userResponseService);
    }

    @Test
    public void respond_forbidden() {
        ResponseEntity responseEntity = controller.respond(2L, 3L, "YES", new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void respond() {
        ResponseEntity responseEntity = controller.respond(1L, 2L, "YES", new TestUserDetails(1L));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userResponseService, Mockito.times(1)).respond(1L, 2L, Response.YES);
    }
}