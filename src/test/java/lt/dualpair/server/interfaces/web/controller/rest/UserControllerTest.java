package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.geo.LocationProvider;
import lt.dualpair.server.domain.model.geo.LocationProviderException;
import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.dto.LocationDTO;
import lt.dualpair.server.interfaces.dto.SearchParametersDTO;
import lt.dualpair.server.interfaces.dto.SociotypeDTO;
import lt.dualpair.server.interfaces.dto.UserDTO;
import lt.dualpair.server.interfaces.dto.assembler.SearchParametersDTOAssembler;
import lt.dualpair.server.interfaces.dto.assembler.UserDTOAssembler;
import lt.dualpair.server.service.user.SocialUserServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController = new UserController();
    private SocialUserServiceImpl socialUserService = mock(SocialUserServiceImpl.class);
    private UserDTOAssembler userDTOAssembler = mock(UserDTOAssembler.class);
    private SearchParametersDTOAssembler searchParametersDTOAssembler = mock(SearchParametersDTOAssembler.class);
    private LocationProvider locationProvider = mock(LocationProvider.class);

    @Before
    public void setUp() throws Exception {
        userController.setSocialUserService(socialUserService);
        userController.setUserDTOAssembler(userDTOAssembler);
        userController.setSearchParametersDTOAssembler(searchParametersDTOAssembler);
        userController.setLocationProvider(locationProvider);
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
        UserDTO userDTO = new UserDTO();
        when(socialUserService.loadUserById(1L)).thenReturn(user);
        when(userDTOAssembler.toDTO(user)).thenReturn(userDTO);
        assertEquals(userDTO, userController.getUser());
    }

    @Test
    public void testUpdateSociotypes() throws Exception {
        SociotypeDTO dto = new SociotypeDTO();
        dto.setCode1("EII");
        SociotypeDTO[] sociotypes = new SociotypeDTO[1];
        sociotypes[0] = dto;
        Set<Sociotype.Code1> sociotypeCodes = new HashSet<>();
        sociotypeCodes.add(Sociotype.Code1.EII);
        ResponseEntity response = userController.setSociotypes(sociotypes);
        verify(socialUserService, times(1)).setUserSociotypes(1L, sociotypeCodes);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/user", response.getHeaders().getLocation().toString());
    }

    @Test
    public void testSetSociotypes_error() throws Exception {
        SociotypeDTO dto = new SociotypeDTO();
        dto.setCode1("EII");
        SociotypeDTO[] sociotypes = new SociotypeDTO[1];
        sociotypes[0] = dto;
        Set<Sociotype.Code1> sociotypeCodes = new HashSet<>();
        sociotypeCodes.add(Sociotype.Code1.EII);
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSociotypes(1L, sociotypeCodes);
        try {
            userController.setSociotypes(sociotypes);
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
        SearchParameters searchParameters = new SearchParameters();
        SearchParametersDTO searchParametersDTO = new SearchParametersDTO();
        when(searchParametersDTOAssembler.toEntity(searchParametersDTO)).thenReturn(searchParameters);
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSearchParameters(1L, searchParameters);
        try {
            userController.setSearchParameters(searchParametersDTO);
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
    }

    @Test
    public void testSetSearchParameters() throws Exception {
        SearchParameters searchParameters = new SearchParameters();
        SearchParametersDTO searchParametersDTO = new SearchParametersDTO();
        when(searchParametersDTOAssembler.toEntity(searchParametersDTO)).thenReturn(searchParameters);
        userController.setSearchParameters(searchParametersDTO);
        verify(socialUserService, times(1)).setUserSearchParameters(1L, searchParameters);
    }

    @Test
    public void testSetLocation_noLatLon() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        try {
            userController.setLocation(locationDTO, 1L);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("\"latitude\" and \"longitude\" must be provided", iae.getMessage());
        }

        locationDTO.setLatitude(1.0);
        try {
            userController.setLocation(locationDTO, 1L);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("\"latitude\" and \"longitude\" must be provided", iae.getMessage());
        }

        locationDTO.setLatitude(null);
        locationDTO.setLongitude(1.0);
        try {
            userController.setLocation(locationDTO, 1L);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("\"latitude\" and \"longitude\" must be provided", iae.getMessage());
        }
    }

    @Test
    public void testSetLocation_locationProviderException() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLatitude(1.0);
        locationDTO.setLongitude(2.0);
        when(locationProvider.getLocation(1.0, 2.0)).thenThrow(new LocationProviderException("Error"));
        try {
            userController.setLocation(locationDTO, 1L);
            fail();
        } catch (LocationProviderException lpe) {
            assertEquals("Error", lpe.getMessage());
        }
        verify(socialUserService, never()).addLocation(any(Long.class), any(Location.class));
    }

    @Test
    public void testSetLocation_invalidUser() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLatitude(1.0);
        locationDTO.setLongitude(2.0);
        try {
            userController.setLocation(locationDTO, 2L);
            fail();
        } catch (ForbiddenException e) {
            assertEquals("Illegal access", e.getMessage());
        }
        verify(locationProvider, never()).getLocation(any(Double.class), any(Double.class));
    }

    @Test
    public void testSetLocation_addLocationException() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLatitude(1.0);
        locationDTO.setLongitude(2.0);
        Location location = new Location(1.0, 2.0, "LT", "Vilnius");
        when(locationProvider.getLocation(1.0, 2.0)).thenReturn(location);
        doThrow(new RuntimeException("Error")).when(socialUserService).addLocation(1L, location);
        try {
            userController.setLocation(locationDTO, 1L);
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
    }

    @Test
    public void testSetLocation() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLatitude(1.0);
        locationDTO.setLongitude(2.0);
        Location location = new Location(1.0, 2.0, "LT", "Vilnius");
        when(locationProvider.getLocation(1.0, 2.0)).thenReturn(location);
        userController.setLocation(locationDTO, 1L);
        verify(socialUserService, times(1)).addLocation(1L, location);
    }
}