package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.MatchPartyTestUtils;
import lt.dualpair.server.domain.model.match.Response;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.infrastructure.persistence.repository.MatchPartyRepository;
import lt.dualpair.server.service.match.MatchService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
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
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, null));
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testResponse_invalidUser() throws Exception {
        MatchParty matchParty = MatchPartyTestUtils.createMatchParty(1L, UserTestUtils.createUser(2L), Response.UNDEFINED);
        when(matchPartyRepository.findById(1L)).thenReturn(Optional.of(matchParty));
        ResponseEntity responseEntity = matchPartyController.response(1L, "YES");
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        verify(matchPartyRepository, never()).save(matchParty);
        verify(matchService, never()).sendMutualMatchNotifications(any(Match.class));
    }

    @Test
    public void testResponse_notFound() throws Exception {
        when(matchPartyRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity responseEntity = matchPartyController.response(1L, "YES");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(matchPartyRepository, never()).save(any(MatchParty.class));
        verify(matchService, never()).sendMutualMatchNotifications(any(Match.class));
    }

    @Test
    public void testResponse() throws Exception {
        MatchParty matchParty = MatchPartyTestUtils.createMatchParty(1L, userPrincipal, Response.UNDEFINED);
        Match match = new Match();
        matchParty.setMatch(match);
        when(matchPartyRepository.findById(1L)).thenReturn(Optional.of(matchParty));
        ResponseEntity responseEntity = matchPartyController.response(1L, "YES");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.YES, matchParty.getResponse());
        verify(matchPartyRepository, times(1)).save(matchParty);
        verify(matchService, times(1)).sendMutualMatchNotifications(match);
    }
}