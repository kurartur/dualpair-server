package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.SearchParametersDTO;
import com.artur.dualpair.server.interfaces.dto.SociotypeDTO;
import com.artur.dualpair.server.interfaces.dto.UserDTO;
import com.artur.dualpair.server.interfaces.dto.assembler.SearchParametersDTOAssembler;
import com.artur.dualpair.server.interfaces.dto.assembler.UserDTOAssembler;
import com.artur.dualpair.server.service.user.SocialUserService;
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
    private SocialUserService socialUserService = mock(SocialUserService.class);
    private UserDTOAssembler userDTOAssembler = mock(UserDTOAssembler.class);
    private SearchParametersDTOAssembler searchParametersDTOAssembler = mock(SearchParametersDTOAssembler.class);

    @Before
    public void setUp() throws Exception {
        userController.setSocialUserService(socialUserService);
        userController.setUserDTOAssembler(userDTOAssembler);
        userController.setSearchParametersDTOAssembler(searchParametersDTOAssembler);
        User user = new User();
        user.setId(1L);
        user.setUsername("1");
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
        when(socialUserService.getUser("1")).thenReturn(user);
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
        verify(socialUserService, times(1)).setUserSociotypes("1", sociotypeCodes);
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
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSociotypes("1", sociotypeCodes);
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
        verify(socialUserService, times(1)).setUserDateOfBirth("1", dateOfBirth);
        assertEquals(HttpStatus.SEE_OTHER, responseEntity.getStatusCode());
        assertEquals("/api/user", responseEntity.getHeaders().getLocation().toString());
    }

    @Test
    public void testSetDateOfBirth_exception() throws Exception {
        Date dateOfBirth = new Date();
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserDateOfBirth("1", dateOfBirth);
        try {
            userController.setDateOfBirth(dateOfBirth);
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
        verify(socialUserService, times(1)).setUserDateOfBirth("1", dateOfBirth);
    }

    @Test
    public void testSetSearchParameters_exception() throws Exception {
        SearchParameters searchParameters = new SearchParameters();
        SearchParametersDTO searchParametersDTO = new SearchParametersDTO();
        when(searchParametersDTOAssembler.toEntity(searchParametersDTO)).thenReturn(searchParameters);
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSearchParameters("1", searchParameters);
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
        verify(socialUserService, times(1)).setUserSearchParameters("1", searchParameters);
    }
}