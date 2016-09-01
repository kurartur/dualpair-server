package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchRequestException;
import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserTestUtils;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class MatchControllerTest {

    private MatchController matchController = new MatchController();
    private MatchService matchService = mock(MatchService.class);
    private MatchResourceAssembler matchResourceAssembler = mock(MatchResourceAssembler.class);
    private User userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = UserTestUtils.createUser(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, null));
        matchController.setMatchService(matchService);
        matchController.setMatchResourceAssembler(matchResourceAssembler);
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testNext_exception() throws Exception {
        doThrow(new MatchRequestException("Error")).when(matchService).nextFor(1L, null);
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
        doReturn(match).when(matchService).nextFor(1L, null);
        doReturn(matchResource).when(matchResourceAssembler).toResource(new UserAwareMatch(userPrincipal, match));
        ResponseEntity<MatchResource> responseEntity = matchController.next(null);
        assertEquals(matchResource, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testNext_noMatches() throws Exception {
        ResponseEntity responseEntity = matchController.next(null);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

}