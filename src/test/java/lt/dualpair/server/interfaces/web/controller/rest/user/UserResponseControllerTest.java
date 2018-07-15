package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.*;
import lt.dualpair.server.interfaces.resource.user.UserResponseResourceAssembler;
import lt.dualpair.server.security.TestUserDetails;
import lt.dualpair.server.service.user.UserResponseService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserResponseControllerTest {

    private UserResponseController controller;
    private UserResponseService userResponseService = mock(UserResponseService.class);
    private UserResponseResourceAssembler userResponseResourceAssembler = mock(UserResponseResourceAssembler.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() throws Exception {
        controller = new UserResponseController(userResponseService, userResponseResourceAssembler, false, userRepository);
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
    public void respond_whenFakeMatchesEnabled_respondFromFake() {
        controller = new UserResponseController(userResponseService, userResponseResourceAssembler, true, userRepository);
        User toUser = UserTestUtils.createUser(2L);
        toUser.setDescription("Lorem ipsum FAKE");
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));

        controller.respond(1L, 2L, "YES", new TestUserDetails(1L));

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> toUserIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(userResponseService, times(2)).respond(userIdCaptor.capture(), toUserIdCaptor.capture(), responseCaptor.capture());
        assertEquals(new Long(2), userIdCaptor.getAllValues().get(1));
        assertEquals(new Long(1), toUserIdCaptor.getAllValues().get(1));
    }

    @Test
    public void respond_whenFakeMatchesEnabledAndUserIsNotFake_responseFromFakeIsNotCreated() {
        controller = new UserResponseController(userResponseService, userResponseResourceAssembler, true, userRepository);
        User toUser = UserTestUtils.createUser(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));

        controller.respond(1L, 2L, "YES", new TestUserDetails(1L));

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> toUserIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(userResponseService, times(1)).respond(userIdCaptor.capture(), toUserIdCaptor.capture(), responseCaptor.capture());
        assertEquals(new Long(1), userIdCaptor.getAllValues().get(0));
        assertEquals(new Long(2), toUserIdCaptor.getAllValues().get(0));
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