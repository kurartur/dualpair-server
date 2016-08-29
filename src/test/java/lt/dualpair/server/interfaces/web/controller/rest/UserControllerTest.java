package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.geo.LocationProvider;
import lt.dualpair.server.domain.model.geo.LocationProviderException;
import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.*;
import lt.dualpair.server.service.user.SocialUserServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
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
        userController.setSearchParametersResourceAssembler(searchParametersResourceAssembler);
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
    public void testSetSociotypes() throws Exception {
        String[] codes = {"EII"};
        Set<Sociotype.Code1> sociotypeCodes = new HashSet<>();
        sociotypeCodes.add(Sociotype.Code1.EII);
        ResponseEntity response = userController.setSociotypes(1L, codes);
        verify(socialUserService, times(1)).setUserSociotypes(1L, sociotypeCodes);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/user", response.getHeaders().getLocation().toString());
    }

    @Test
    public void testSetSociotypes_invalidUser() throws Exception {
        String[] codes = {"EII"};
        ResponseEntity response = userController.setSociotypes(2L, codes);
        verify(socialUserService, never()).setUserSociotypes(any(Long.class), any(Set.class));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testSetSociotypes_error() throws Exception {
        String[] codes = {"EII"};
        Set<Sociotype.Code1> sociotypeCodes = new HashSet<>();
        sociotypeCodes.add(Sociotype.Code1.EII);
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSociotypes(1L, sociotypeCodes);
        try {
            userController.setSociotypes(1L, codes);
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
    }

    @Test
    public void testSetDateOfBirth() throws Exception {
        Date dateOfBirth = new Date();
        ResponseEntity responseEntity = userController.setDateOfBirth(dateOfBirth);
        verify(socialUserService, times(1)).setUserDateOfBirth(1L, dateOfBirth);
        assertEquals(HttpStatus.SEE_OTHER, responseEntity.getStatusCode());
        assertEquals("/api/user", responseEntity.getHeaders().getLocation().toString());
    }

    @Test
    public void testSetDateOfBirth_exception() throws Exception {
        Date dateOfBirth = new Date();
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserDateOfBirth(1L, dateOfBirth);
        try {
            userController.setDateOfBirth(dateOfBirth);
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
        verify(socialUserService, times(1)).setUserDateOfBirth(1L, dateOfBirth);
    }

    @Test
    public void testSetSearchParameters_exception() throws Exception {
        SearchParametersResource searchParametersResource = new SearchParametersResource();
        searchParametersResource.setSearchMale(false);
        searchParametersResource.setSearchFemale(false);
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSearchParameters(eq(1L), any(SearchParameters.class));
        try {
            userController.setSearchParameters(1L, searchParametersResource);
            fail();
        } catch (Exception re) {
            assertEquals("Error", re.getMessage());
        }
    }

    @Test
    public void testSetSearchParamters_invalidUser() throws Exception {
        ResponseEntity responseEntity = userController.setSearchParameters(2L, null);
        verify(socialUserService, never()).setUserSearchParameters(any(Long.class), any(SearchParameters.class));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testSetSearchParameters() throws Exception {
        SearchParametersResource searchParametersResource = new SearchParametersResource();
        searchParametersResource.setMinAge(20);
        searchParametersResource.setMaxAge(25);
        searchParametersResource.setSearchFemale(true);
        searchParametersResource.setSearchMale(true);
        userController.setSearchParameters(1L, searchParametersResource);
        ArgumentCaptor<SearchParameters> captor = ArgumentCaptor.forClass(SearchParameters.class);
        verify(socialUserService, times(1)).setUserSearchParameters(eq(1L), captor.capture());
        SearchParameters searchParameters = captor.getValue();
        assertEquals((Integer)20, searchParameters.getMinAge());
        assertEquals((Integer)25, searchParameters.getMaxAge());
        assertTrue(searchParameters.getSearchFemale());
        assertTrue(searchParameters.getSearchMale());
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

    @Test
    public void testGetSearchParameters() throws Exception {
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        user.setSearchParameters(searchParameters);
        when(socialUserService.loadUserById(1L)).thenReturn(user);
        SearchParametersResource resource = new SearchParametersResource();
        when(searchParametersResourceAssembler.toResource(searchParameters)).thenReturn(resource);
        ResponseEntity<SearchParametersResource> response = userController.getSearchParameters(1L);
        assertEquals(resource, response.getBody());
    }

    @Test
    public void testGetSearchParameters_invalidUser() throws Exception {
        ResponseEntity<SearchParametersResource> response = userController.getSearchParameters(2L);
        verify(socialUserService, never()).loadUserById(any(Long.class));
        verify(searchParametersResourceAssembler, never()).toResource(any(SearchParameters.class));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}