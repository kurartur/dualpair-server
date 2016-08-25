package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.MatchRequestException;
import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.dto.MatchDTO;
import lt.dualpair.server.interfaces.dto.assembler.MatchDTOAssembler;
import lt.dualpair.server.interfaces.resource.match.MatchResource;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.service.match.MatchService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MatchControllerTest {

    private MatchController matchController = new MatchController();
    private MatchService matchService = mock(MatchService.class);
    private MatchDTOAssembler matchDTOAssembler = mock(MatchDTOAssembler.class);
    private MatchResourceAssembler matchResourceAssembler = mock(MatchResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(crateUser(1L, "username"), null));
        matchController.setMatchService(matchService);
        matchController.setMatchDTOAssembler(matchDTOAssembler);
        matchController.setMatchResourceAssembler(matchResourceAssembler);
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testResponse() throws Exception {
        ResponseEntity responseEntity = matchController.response(1L, "YES");
        verify(matchService, times(1)).responseByUser(1L, MatchParty.Response.YES, 1L);
        assertEquals(HttpStatus.SEE_OTHER, responseEntity.getStatusCode());
        assertEquals("/api/match/1", responseEntity.getHeaders().getLocation().toString());
    }

    @Test
    public void testResponse_exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(matchService).responseByUser(1L, MatchParty.Response.YES, 1L);
        try {
            matchController.response(1L, "YES");
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
    }

    @Test
    public void testResponse_invalidResponseValue() throws Exception {
        try {
            matchController.response(1L, "INVALID");
            fail();
        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().contains("No enum constant"));
        }
        verify(matchService, times(0)).responseByUser(any(Long.class), any(MatchParty.Response.class), any(Long.class));
    }

    @Test
    public void testNext_exception() throws Exception {
        doThrow(new MatchRequestException("Error")).when(matchService).nextFor(1L);
        try {
            matchController.next(null);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Error", mre.getMessage());
        }
    }

    @Test
    public void testNext() throws Exception {
        MatchResource matchResource= new MatchResource();
        Match match = new Match();
        doReturn(match).when(matchService).nextFor(1L);
        doReturn(matchResource).when(matchResourceAssembler).toResource(any(UserAwareMatch.class));
        ResponseEntity<MatchResource> responseEntity = matchController.next(null);
        assertEquals(matchResource, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testNext_noMatches() throws Exception {
        ResponseEntity responseEntity = matchController.next(null);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testMatch_exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(matchService).getUserMatch(1L, 1L);
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
        doReturn(match).when(matchService).getUserMatch(1L, 1L);
        doReturn(matchDTO).when(matchDTOAssembler).toDTO(match);
        ResponseEntity<MatchDTO> response = matchController.match(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(matchDTO, response.getBody());
    }

    @Test
    public void testMatch_notFound() throws Exception {
        ResponseEntity response = matchController.match(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testMatches() throws Exception {
        MatchDTO matchDTO = new MatchDTO();
        Set<MatchDTO> matchDTOs = new HashSet<>();
        matchDTOs.add(matchDTO);
        Match match = new Match();
        Set<Match> matches = new HashSet<>();
        matches.add(match);
        doReturn(matches).when(matchService).getUserMutualMatches(1L);
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