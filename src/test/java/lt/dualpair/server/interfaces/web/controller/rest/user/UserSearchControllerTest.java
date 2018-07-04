package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRequest;
import lt.dualpair.core.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.security.UserDetailsImpl;
import lt.dualpair.server.service.user.UserSearchService;
import lt.dualpair.server.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserSearchControllerTest {

    private UserSearchController userSearchController;
    private UserSearchService userSearchService = mock(UserSearchService.class);
    private UserResourceAssembler userResourceAssembler = mock(UserResourceAssembler.class);
    private UserService userService = mock(UserService.class);
    private User userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = UserTestUtils.createUser(1L);
        userSearchController = new UserSearchController(userSearchService, userResourceAssembler, userService);
    }

    @Test
    public void testFind_notFound() throws Exception {
        doReturn(UserTestUtils.createUser()).when(userService).loadUserById(1L);
        doReturn(Optional.empty()).when(userSearchService).findOne(any(UserRequest.class));
        ResponseEntity responseEntity = userSearchController.find(crateSearchQuery(), new UserDetailsImpl(userPrincipal));
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testFind() throws Exception {
        User user = UserTestUtils.createUser();
        doReturn(user).when(userService).loadUserById(1L);
        UserResource userResource = new UserResource();
        User found = new User();
        doReturn(Optional.of(found)).when(userSearchService).findOne(any(UserRequest.class));
        doReturn(userResource).when(userResourceAssembler).toResource(found);
        ResponseEntity<UserResource> responseEntity = userSearchController.find(crateSearchQuery(), new UserDetailsImpl(userPrincipal));
        assertEquals(userResource, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ArgumentCaptor<UserRequest> requestArgumentCaptor = ArgumentCaptor.forClass(UserRequest.class);
        verify(userSearchService, times(1)).findOne(requestArgumentCaptor.capture());
        UserRequest userRequest = requestArgumentCaptor.getValue();
        assertEquals(25, userRequest.getMinAge());
        assertEquals(30, userRequest.getMaxAge());
        assertEquals(new HashSet<>(Arrays.asList(Gender.MALE, Gender.FEMALE)), userRequest.getGenders());
        assertEquals(new HashSet<>(Arrays.asList(5L)), userRequest.getExcludedOpponentIds());
        assertEquals(userRequest.getUser(), user);
        assertEquals(10.0, userRequest.getLatitude(), 0);
        assertEquals(11.0, userRequest.getLongitude(), 0);
        assertEquals("LT", userRequest.getCountryCode());
    }

    private UserSearchController.SearchQuery crateSearchQuery() {
        UserSearchController.SearchQuery searchQuery = new UserSearchController.SearchQuery();
        searchQuery.setMinAge(25);
        searchQuery.setMaxAge(30);
        searchQuery.setSearchMale("Y");
        searchQuery.setSearchFemale("Y");
        searchQuery.setExcludeOpponents(Arrays.asList(5L));
        return searchQuery;
    }

}