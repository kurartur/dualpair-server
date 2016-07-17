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

import java.util.HashSet;
import java.util.Set;

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
        ResponseEntity responseEntity = matchController.response(1L, "YES");
        verify(matchService, times(1)).responseByUser(1L, Match.Response.YES, "username");
        assertEquals(HttpStatus.SEE_OTHER, responseEntity.getStatusCode());
        assertEquals("/api/match/1", responseEntity.getHeaders().getLocation().toString());
    }

    @Test
    public void testResponse_exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(matchService).responseByUser(1L, Match.Response.YES, "username");
        try {
            matchController.response(1L, "YES");
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
        verify(matchService, times(1)).responseByUser(1L, Match.Response.YES, "username");
    }

    @Test
    public void testResponse_invalidResponseValue() throws Exception {
        try {
            matchController.response(1L, "INVALID");
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("No enum constant com.artur.dualpair.server.domain.model.Match.Response.INVALID", iae.getMessage());
        }
        verify(matchService, times(0)).responseByUser(any(Long.class), any(Match.Response.class), any(String.class));
    }

    @Test
    public void testNext_exception() throws Exception {
        doThrow(new MatchRequestException("Error")).when(matchService).nextFor("username");
        try {
            matchController.next();
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Error", mre.getMessage());
        }
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
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testMatch_exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(matchService).getUserMatch(1L, "username");
        try {
            matchController.match(1L);
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
    }

    @Test
    public void testMatch() throws Exception {
        MatchDTO matchDTO = new MatchDTO();
        Match match = new Match();
        doReturn(match).when(matchService).getUserMatch(1L, "username");
        doReturn(matchDTO).when(matchDTOAssembler).toDTO(match);
        ResponseEntity<MatchDTO> response = matchController.match(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(matchDTO, response.getBody());
    }

    @Test
    public void testMatches() throws Exception {
        MatchDTO matchDTO = new MatchDTO();
        Set<MatchDTO> matchDTOs = new HashSet<>();
        matchDTOs.add(matchDTO);
        Match match = new Match();
        Set<Match> matches = new HashSet<>();
        matches.add(match);
        doReturn(matches).when(matchService).getUserMatches("username");
        doReturn(matchDTOs).when(matchDTOAssembler).toDTOSet(matches);
        ResponseEntity<Set<MatchDTO>> response = matchController.matches();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(matchDTOs, response.getBody());
        assertEquals(1, response.getBody().size());
    }

    private User crateUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}