package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchRequestException;
import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.match.MatchResource;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.service.match.MatchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        matchController.setMatchService(matchService);
        matchController.setMatchResourceAssembler(matchResourceAssembler);
    }

    @Test
    public void testNext_exception() throws Exception {
        doThrow(new MatchRequestException("Error")).when(matchService).nextFor(1L, null);
        try {
            matchController.next(null, userPrincipal);
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
        ResponseEntity<MatchResource> responseEntity = matchController.next(null, userPrincipal);
        assertEquals(matchResource, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testNext_noMatches() throws Exception {
        ResponseEntity responseEntity = matchController.next(null, userPrincipal);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

}