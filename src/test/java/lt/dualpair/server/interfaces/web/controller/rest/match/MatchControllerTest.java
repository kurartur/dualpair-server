package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchRequest;
import lt.dualpair.core.match.MatchRequestException;
import lt.dualpair.core.match.UserAwareMatch;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.match.MatchResource;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.security.UserDetailsImpl;
import lt.dualpair.server.service.match.MatchService;
import lt.dualpair.server.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class MatchControllerTest {

    private MatchController matchController;
    private MatchService matchService = mock(MatchService.class);
    private MatchResourceAssembler matchResourceAssembler = mock(MatchResourceAssembler.class);
    private UserService userService = mock(UserService.class);
    private User userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = UserTestUtils.createUser(1L);
        matchController = new MatchController(matchService, matchResourceAssembler, userService);
    }

    @Test
    public void testNext_exception() throws Exception {
        User user = UserTestUtils.createUser();
        doReturn(user).when(userService).loadUserById(1L);
        doThrow(new MatchRequestException("Error")).when(matchService).nextFor(any(MatchRequest.class));
        try {
            matchController.next(crateSearchQuery(), new UserDetailsImpl(userPrincipal));
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Error", mre.getMessage());
        }
    }

    @Test
    public void testNext() throws Exception {
        User user = UserTestUtils.createUser();
        doReturn(user).when(userService).loadUserById(1L);
        MatchResource matchResource= new MatchResource();
        Match match = new Match();
        doReturn(match).when(matchService).nextFor(any(MatchRequest.class));
        doReturn(matchResource).when(matchResourceAssembler).toResource(new UserAwareMatch(userPrincipal, match));
        ResponseEntity<MatchResource> responseEntity = matchController.next(crateSearchQuery(), new UserDetailsImpl(userPrincipal));
        assertEquals(matchResource, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ArgumentCaptor<MatchRequest> matchRequestArgumentCaptor = ArgumentCaptor.forClass(MatchRequest.class);
        verify(matchService, times(1)).nextFor(matchRequestArgumentCaptor.capture());
        MatchRequest matchRequest = matchRequestArgumentCaptor.getValue();
        assertEquals(25, matchRequest.getMinAge());
        assertEquals(30, matchRequest.getMaxAge());
        assertEquals(new HashSet<>(Arrays.asList(Gender.MALE, Gender.FEMALE)), matchRequest.getGenders());
        assertEquals(Arrays.asList(5L), matchRequest.getExcludedOpponentIds());
        assertEquals(matchRequest.getUser(), user);
        assertEquals(10.0, matchRequest.getLatitude(), 0);
        assertEquals(11.0, matchRequest.getLongitude(), 0);
        assertEquals("LT", matchRequest.getCountryCode());
    }

    @Test
    public void testNext_noMatches() throws Exception {
        doReturn(UserTestUtils.createUser()).when(userService).loadUserById(1L);
        ResponseEntity responseEntity = matchController.next(crateSearchQuery(), new UserDetailsImpl(userPrincipal));
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    private MatchController.SearchQuery crateSearchQuery() {
        MatchController.SearchQuery searchQuery = new MatchController.SearchQuery();
        searchQuery.setMinAge(25);
        searchQuery.setMaxAge(30);
        searchQuery.setSearchMale("Y");
        searchQuery.setSearchFemale("Y");
        searchQuery.setExcludeOpponents(Arrays.asList(5L));
        return searchQuery;
    }

}