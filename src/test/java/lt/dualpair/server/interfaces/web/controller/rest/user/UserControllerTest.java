package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.geo.LocationProvider;
import lt.dualpair.server.domain.model.geo.LocationProviderException;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.LocationResource;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResourceAssembler;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.service.user.SocialUserServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController = new UserController();
    private SocialUserServiceImpl socialUserService = mock(SocialUserServiceImpl.class);
    private SearchParametersResourceAssembler searchParametersResourceAssembler = mock(SearchParametersResourceAssembler.class);
    private LocationProvider locationProvider = mock(LocationProvider.class);
    private UserResourceAssembler userResourceAssembler = mock(UserResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        userController.setSocialUserService(socialUserService);
        userController.setLocationProvider(locationProvider);
        userController.setUserResourceAssembler(userResourceAssembler);
        User user = new User();
        user.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testGetUser() throws Exception {
        User user = new User();
        UserResource userResource = new UserResource();
        when(socialUserService.loadUserById(1L)).thenReturn(user);
        when(userResourceAssembler.toResource(user)).thenReturn(userResource);
        assertEquals(userResource, userController.getUser());
    }

    @Test
    public void testUpdateUser_invalidUser() throws Exception {
        ResponseEntity responseEntity = userController.updateUser(2L, new HashMap<>());
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
        User user = new User();
        when(socialUserService.loadUserById(1L)).thenReturn(user);
        ResponseEntity responseEntity = userController.updateUser(1L, data);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals("/api/user/1", responseEntity.getHeaders().getLocation().toString());
        verify(socialUserService, times(1)).updateUser(user);
        assertEquals("descr", user.getDescription());
        assertEquals("name", user.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        assertEquals("1990-07-01T12:13:14", sdf.format(user.getDateOfBirth()));
    }

    @Test
    public void testSetDateOfBirth() throws Exception {
        Date dateOfBirth = new Date();
        ResponseEntity responseEntity = userController.setDateOfBirth(1L, dateOfBirth);
        verify(socialUserService, times(1)).setUserDateOfBirth(1L, dateOfBirth);
        assertEquals(HttpStatus.SEE_OTHER, responseEntity.getStatusCode());
        assertEquals("/api/user", responseEntity.getHeaders().getLocation().toString());
    }

    @Test
    public void testSetDateOfBirth_exception() throws Exception {
        Date dateOfBirth = new Date();
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserDateOfBirth(1L, dateOfBirth);
        try {
            userController.setDateOfBirth(1L, dateOfBirth);
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
        verify(socialUserService, times(1)).setUserDateOfBirth(1L, dateOfBirth);
    }

    @Test
    public void testSetDateOfBirth_invalidUser() throws Exception {
        Date dateOfBirth = new Date();
        ResponseEntity response = userController.setDateOfBirth(2L, dateOfBirth);
        verify(socialUserService, never()).setUserDateOfBirth(any(Long.class), any(Date.class));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testSetLocation_noLatLon() throws Exception {
        LocationResource locationResource = new LocationResource();
        try {
            userController.setLocation(locationResource, 1L);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("\"latitude\" and \"longitude\" must be provided", iae.getMessage());
        }

        locationResource.setLatitude(1.0);
        try {
            userController.setLocation(locationResource, 1L);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("\"latitude\" and \"longitude\" must be provided", iae.getMessage());
        }

        locationResource.setLatitude(null);
        locationResource.setLongitude(1.0);
        try {
            userController.setLocation(locationResource, 1L);
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
            userController.setLocation(locationResource, 1L);
            fail();
        } catch (LocationProviderException lpe) {
            assertEquals("Error", lpe.getMessage());
        }
        verify(socialUserService, never()).addLocation(any(Long.class), any(Location.class));
    }

    @Test
    public void testSetLocation_invalidUser() throws Exception {
        ResponseEntity response = userController.setLocation(null, 2L);
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
            userController.setLocation(locationResource, 1L);
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
        userController.setLocation(locationResource, 1L);
        verify(socialUserService, times(1)).addLocation(1L, location);
    }

}