package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchParty;
import lt.dualpair.core.match.MatchPartyTestUtils;
import lt.dualpair.core.match.Response;
import lt.dualpair.core.user.MatchPartyRepository;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserTestUtils;
import lt.dualpair.server.security.UserDetailsImpl;
import lt.dualpair.server.service.match.MatchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MatchPartyControllerTest {

    private MatchPartyController matchPartyController = new MatchPartyController();
    private MatchService matchService = mock(MatchService.class);
    private MatchPartyRepository matchPartyRepository = mock(MatchPartyRepository.class);
    private User userPrincipal;

    @Before
    public void setUp() throws Exception {
        matchPartyController.setMatchService(matchService);
        matchPartyController.setMatchPartyRepository(matchPartyRepository);
        userPrincipal = UserTestUtils.createUser(1L);
    }

    @Test
    public void testResponse_invalidUser() throws Exception {
        MatchParty matchParty = MatchPartyTestUtils.createMatchParty(1L, UserTestUtils.createUser(2L), Response.UNDEFINED);
        when(matchPartyRepository.findById(1L)).thenReturn(Optional.of(matchParty));
        ResponseEntity responseEntity = matchPartyController.response(1L, "YES", new UserDetailsImpl(userPrincipal));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        verify(matchPartyRepository, never()).save(matchParty);
        verify(matchService, never()).sendMutualMatchNotifications(any(Match.class));
    }

    @Test
    public void testResponse_notFound() throws Exception {
        when(matchPartyRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity responseEntity = matchPartyController.response(1L, "YES", new UserDetailsImpl(userPrincipal));
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(matchPartyRepository, never()).save(any(MatchParty.class));
        verify(matchService, never()).sendMutualMatchNotifications(any(Match.class));
    }

    @Test
    public void testResponse() throws Exception {
        MatchParty matchParty = MatchPartyTestUtils.createMatchParty(1L, userPrincipal, Response.UNDEFINED);
        Match match = new Match();
        matchParty.setMatch(match);
        match.setMatchParties(matchParty, MatchPartyTestUtils.createMatchParty(2L, new User(), Response.UNDEFINED));
        when(matchPartyRepository.findById(1L)).thenReturn(Optional.of(matchParty));
        ResponseEntity responseEntity = matchPartyController.response(1L, "YES", new UserDetailsImpl(userPrincipal));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.YES, matchParty.getResponse());
        verify(matchPartyRepository, times(1)).save(matchParty);
        verify(matchService, times(1)).sendMutualMatchNotifications(match);
    }

    @Test
    public void testResponse_mutual() throws Exception {
        MatchParty matchParty = MatchPartyTestUtils.createMatchParty(1L, userPrincipal, Response.UNDEFINED);
        Match match = new Match();
        assertNull(match.getDateBecameMutual());
        matchParty.setMatch(match);
        match.setMatchParties(matchParty, MatchPartyTestUtils.createMatchParty(2L, new User(), Response.YES));
        when(matchPartyRepository.findById(1L)).thenReturn(Optional.of(matchParty));
        ResponseEntity responseEntity = matchPartyController.response(1L, "YES", new UserDetailsImpl(userPrincipal));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.YES, matchParty.getResponse());
        assertNotNull(match.getDateBecameMutual());
        verify(matchPartyRepository, times(1)).save(matchParty);
        verify(matchService, times(1)).sendMutualMatchNotifications(match);
    }
}