package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.Response;
import lt.dualpair.core.user.UserResponse;
import lt.dualpair.server.interfaces.resource.user.UserResponseResourceAssembler;
import lt.dualpair.server.security.TestUserDetails;
import lt.dualpair.server.service.user.UserResponseService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserResponseControllerTest {

    private UserResponseController controller;
    private UserResponseService userResponseService = mock(UserResponseService.class);
    private UserResponseResourceAssembler userResponseResourceAssembler = mock(UserResponseResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        controller = new UserResponseController(userResponseService, userResponseResourceAssembler);
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
        verify(userResponseService, times(1)).respond(1L, 2L, Response.YES);
    }

    @Test
    public void responses_forbidden() {
        ResponseEntity responseEntity = controller.responses(2L, null, null, new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void responses() {
        Pageable pageable = mock(Pageable.class);
        PagedResourcesAssembler<UserResponse> pagedResourcesAssembler = mock(PagedResourcesAssembler.class);
        Page<UserResponse> page = new PageImpl<>(Collections.singletonList(new UserResponse()));
        when(userResponseService.getResponsesPage(1L, pageable)).thenReturn(page);
        ResponseEntity responseEntity = controller.responses(1L, pageable, pagedResourcesAssembler, new TestUserDetails(1L));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(pagedResourcesAssembler, times(1)).toResource(page, userResponseResourceAssembler);
    }
}