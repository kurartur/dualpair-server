package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.MatchDTO;
import com.artur.dualpair.server.interfaces.dto.assembler.MatchDTOAssembler;
import com.artur.dualpair.server.service.match.MatchRequestException;
import com.artur.dualpair.server.service.match.MatchService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class MatchControllerTest {

    private MatchController matchController = new MatchController();
    private MatchService matchService = mock(MatchService.class);
    private MatchDTOAssembler matchDTOAssembler = mock(MatchDTOAssembler.class);

    @Before
    public void setUp() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(crateUser(1L, "username"), null));
        matchController.setMatchService(matchService);
        matchController.setMatchDTOAssembler(matchDTOAssembler);
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testResponse() throws Exception {
        matchController.response(1L, "YES");
        verify(matchService, times(1)).responseByUser(1L, Match.Response.YES, "username");
    }

    @Test
    public void testResponse_invalidUser() throws Exception {
        doThrow(new IllegalArgumentException("Invalid user")).when(matchService).responseByUser(1L, Match.Response.YES, "username");
        try {
            matchController.response(1L, "YES");
            fail();
        } catch (InsufficientPrivilegesException ipe) {
            assertEquals(InsufficientPrivilegesException.illegalAccess, ipe.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResponse_invalidResponseValue() throws Exception {
        matchController.response(1L, "INVALID");
    }

    @Test
    public void testNext_exception() throws Exception {
        doThrow(new MatchRequestException("Error")).when(matchService).nextFor("username");
        ResponseEntity responseEntity = matchController.next();
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody());
    }

    @Test
    public void testNext() throws Exception {
        MatchDTO matchDTO = new MatchDTO();
        Match match = new Match();
        doReturn(match).when(matchService).nextFor("username");
        doReturn(matchDTO).when(matchDTOAssembler).toDTO(match);
        ResponseEntity<MatchDTO> responseEntity = matchController.next();
        assertEquals(matchDTO, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testNext_noMatches() throws Exception {
        ResponseEntity responseEntity = matchController.next();
        assertEquals("No matches", responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testList() throws Exception {

    }

    private User crateUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}