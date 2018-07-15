package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.location.Location;
import lt.dualpair.core.location.LocationProvider;
import lt.dualpair.core.location.LocationProviderException;
import lt.dualpair.core.match.Match;
import lt.dualpair.core.user.*;
import lt.dualpair.server.interfaces.resource.user.LocationResource;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.security.TestUserDetails;
import lt.dualpair.server.service.user.SocialUserServiceImpl;
import lt.dualpair.server.service.user.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController = new UserController();
    private SocialUserServiceImpl socialUserService = mock(SocialUserServiceImpl.class);
    private LocationProvider locationProvider = mock(LocationProvider.class);
    private UserResourceAssembler userResourceAssembler = mock(UserResourceAssembler.class);
    private UserResponseRepository userResponseRepository = mock(UserResponseRepository.class);

    @Before
    public void setUp() throws Exception {
        userController.setSocialUserService(socialUserService);
        userController.setLocationProvider(locationProvider);
        userController.setUserResourceAssembler(userResourceAssembler);
        userController.setUserResponseRepository(userResponseRepository);
    }

    @Test
    public void testMe() throws Exception {
        User user = new User();
        UserResource userResource = new UserResource();
        when(socialUserService.loadUserById(1L)).thenReturn(user);
        when(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(user, true, true))).thenReturn(userResource);
        ResponseEntity responseEntity = userController.me(new TestUserDetails(1L));
        assertEquals(userResource, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testMe_notFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(socialUserService).loadUserById(1L);
        ResponseEntity responseEntity = userController.me(new TestUserDetails(1L));
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    public void testGetUser() throws Exception {
        User user = UserTestUtils.createUser(2L);
        UserResource userResource = new UserResource();
        when(socialUserService.loadUserById(2L)).thenReturn(user);
        when(userResponseRepository.findByParties(1L, 2L)).thenReturn(Optional.of(new UserResponse()));
        when(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(user, false, false))).thenReturn(userResource);
        ResponseEntity responseEntity = userController.getUser(2L, new TestUserDetails(1L));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResource, responseEntity.getBody());
    }

    @Test
    public void testGetUser_whenDidntRespond_forbidden() {
        User user = UserTestUtils.createUser(2L);
        UserResource userResource = new UserResource();
        when(socialUserService.loadUserById(2L)).thenReturn(user);
        when(userResponseRepository.findByParties(1L, 2L)).thenReturn(Optional.empty());
        ResponseEntity responseEntity = userController.getUser(2L, new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testGetUser_whenIsMatch_passMatchToContext() {
        User user = UserTestUtils.createUser(2L);
        UserResource userResource = new UserResource();
        when(socialUserService.loadUserById(2L)).thenReturn(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setMatch(new Match());
        when(userResponseRepository.findByParties(1L, 2L)).thenReturn(Optional.of(userResponse));
        when(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(user, true, false))).thenReturn(userResource);
        ResponseEntity responseEntity = userController.getUser(2L, new TestUserDetails(1L));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResource, responseEntity.getBody());
    }

    @Test
    public void testUpdateUser_invalidUser() throws Exception {
        ResponseEntity responseEntity = userController.updateUser(2L, new HashMap<>(), new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        verify(socialUserService, never()).loadUserById(any(Long.class));
        verify(socialUserService, never()).updateUser(any(User.class));
    }

    @Test
    public void testUpdateUser() throws Exception {
        HashMap<String, Object> data = new HashMap<>();
        data.put("description", "descr");
        data.put("name", "name");
        data.put("dateOfBirth", "1990-07-01T12:13:14");
        data.put("relationshipStatus", "SI");
        data.put("purposesOfBeing", Arrays.asList("FIFR", "FILO"));
        User user = new User();
        when(socialUserService.loadUserById(1L)).thenReturn(user);
        ResponseEntity responseEntity = userController.updateUser(1L, data, new TestUserDetails(1L));
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals("/api/user/1", responseEntity.getHeaders().getLocation().toString());
        verify(socialUserService, times(1)).updateUser(user);
        assertEquals("descr", user.getDescription());
        assertEquals("name", user.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        assertEquals("1990-07-01T12:13:14", sdf.format(user.getDateOfBirth()));
        assertEquals(RelationshipStatus.SINGLE, user.getRelationshipStatus());
        Set<PurposeOfBeing> purposes = user.getPurposesOfBeing();
        assertEquals(2, purposes.size());
        assertTrue(purposes.contains(PurposeOfBeing.FIND_FRIEND));
        assertTrue(purposes.contains(PurposeOfBeing.FIND_LOVE));
    }

    @Test
    public void testUpdateUser_whenRelationshipStatusEmpty_setAsNone() throws Exception {
        HashMap<String, Object> data = new HashMap<>();
        data.put("relationshipStatus", "");
        User user = new User();
        user.setRelationshipStatus(RelationshipStatus.SINGLE);
        when(socialUserService.loadUserById(1L)).thenReturn(user);
        ResponseEntity responseEntity = userController.updateUser(1L, data, new TestUserDetails(1L));
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(socialUserService, times(1)).updateUser(user);
        assertEquals(RelationshipStatus.NONE, user.getRelationshipStatus());
    }

    @Test
    public void testSetDateOfBirth() throws Exception {
        Date dateOfBirth = new Date();
        ResponseEntity responseEntity = userController.setDateOfBirth(1L, dateOfBirth, new TestUserDetails(1L));
        verify(socialUserService, times(1)).setUserDateOfBirth(1L, dateOfBirth);
        assertEquals(HttpStatus.SEE_OTHER, responseEntity.getStatusCode());
        assertEquals("/api/user", responseEntity.getHeaders().getLocation().toString());
    }

    @Test
    public void testSetDateOfBirth_exception() throws Exception {
        Date dateOfBirth = new Date();
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserDateOfBirth(1L, dateOfBirth);
        try {
            userController.setDateOfBirth(1L, dateOfBirth, new TestUserDetails(1L));
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
        verify(socialUserService, times(1)).setUserDateOfBirth(1L, dateOfBirth);
    }

    @Test
    public void testSetDateOfBirth_invalidUser() throws Exception {
        Date dateOfBirth = new Date();
        ResponseEntity response = userController.setDateOfBirth(2L, dateOfBirth, new TestUserDetails(1L));
        verify(socialUserService, never()).setUserDateOfBirth(any(Long.class), any(Date.class));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testSetLocation_noLatLon() throws Exception {
        LocationResource locationResource = new LocationResource();
        try {
            userController.setLocation(locationResource, 1L, new TestUserDetails(1L));
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("\"latitude\" and \"longitude\" must be provided", iae.getMessage());
        }

        locationResource.setLatitude(1.0);
        try {
            userController.setLocation(locationResource, 1L, new TestUserDetails(1L));
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("\"latitude\" and \"longitude\" must be provided", iae.getMessage());
        }

        locationResource.setLatitude(null);
        locationResource.setLongitude(1.0);
        try {
            userController.setLocation(locationResource, 1L, new TestUserDetails(1L));
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("\"latitude\" and \"longitude\" must be provided", iae.getMessage());
        }
    }

    @Test
    public void testSetLocation_locationProviderException() throws Exception {
        LocationResource locationResource = new LocationResource();
        locationResource.setLatitude(1.0);
        locationResource.setLongitude(2.0);
        when(locationProvider.getLocation(1.0, 2.0)).thenThrow(new LocationProviderException("Error"));
        try {
            userController.setLocation(locationResource, 1L, new TestUserDetails(1L));
            fail();
        } catch (LocationProviderException lpe) {
            assertEquals("Error", lpe.getMessage());
        }
        verify(socialUserService, never()).addLocation(any(Long.class), any(Location.class));
    }

    @Test
    public void testSetLocation_invalidUser() throws Exception {
        ResponseEntity response = userController.setLocation(null, 2L, new TestUserDetails(1L));
        verify(locationProvider, never()).getLocation(any(Double.class), any(Double.class));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testSetLocation_addLocationException() throws Exception {
        LocationResource locationResource = new LocationResource();
        locationResource.setLatitude(1.0);
        locationResource.setLongitude(2.0);
        Location location = new Location(1.0, 2.0, "LT", "Vilnius");
        when(locationProvider.getLocation(1.0, 2.0)).thenReturn(location);
        doThrow(new RuntimeException("Error")).when(socialUserService).addLocation(1L, location);
        try {
            userController.setLocation(locationResource, 1L, new TestUserDetails(1L));
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
    }

    @Test
    public void testSetLocation() throws Exception {
        LocationResource locationResource = new LocationResource();
        locationResource.setLatitude(1.0);
        locationResource.setLongitude(2.0);
        Location location = new Location(1.0, 2.0, "LT", "Vilnius");
        when(locationProvider.getLocation(1.0, 2.0)).thenReturn(location);
        userController.setLocation(locationResource, 1L, new TestUserDetails(1L));
        verify(socialUserService, times(1)).addLocation(1L, location);
    }

}