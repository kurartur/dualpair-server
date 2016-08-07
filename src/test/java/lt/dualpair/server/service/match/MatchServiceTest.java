package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.match.*;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.interfaces.web.controller.rest.ForbiddenException;
import lt.dualpair.server.service.user.UserService;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static lt.dualpair.server.domain.model.match.MatchPartyTestUtils.createMatchParty;
import static lt.dualpair.server.domain.model.user.UserTestUtils.createUser;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MatchServiceTest {

    private MatchService matchService = new MatchService();
    private DefaultMatchFinder defaultMatchFinder = mock(DefaultMatchFinder.class);
    private RepositoryMatchFinder repositoryMatchFinder = mock(RepositoryMatchFinder.class);
    private UserService userService = mock(UserService.class);
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private MatchRequestValidator matchRequestValidator = mock(MatchRequestValidator.class);

    @Before
    public void setUp() throws Exception {
        matchService.setDefaultMatchFinder(defaultMatchFinder);
        matchService.setRepositoryMatchFinder(repositoryMatchFinder);
        matchService.setUserService(userService);
        matchService.setMatchRepository(matchRepository);
        matchService.setMatchRequestValidator(matchRequestValidator);
    }

    @Test
    public void testNextFor_hasMatchInRepo() throws Exception {
        Match match = new Match();
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(10);
        searchParameters.setMaxAge(20);
        searchParameters.setSearchFemale(true);
        searchParameters.setLocation(new Location(10.0, 10.0, "LT", "City"));
        user.setSearchParameters(searchParameters);
        doReturn(match).when(repositoryMatchFinder).findOne(any(MatchRequest.class));
        when(userService.loadUserById(1L)).thenReturn(user);
        Match resultMatch = matchService.nextFor(1L);
        verify(userService, times(1)).loadUserById(1L);
        verifyNoMoreInteractions(defaultMatchFinder);
        verify(matchRepository, times(1)).save(match);
        verify(matchRequestValidator, times(1)).validateMatchRequest(user, searchParameters);
        assertEquals(match, resultMatch);
    }

    @Test
    public void testNextFor_hasMatchInDefault() throws Exception {
        Match match = new Match();
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(10);
        searchParameters.setMaxAge(20);
        searchParameters.setSearchFemale(true);
        searchParameters.setLocation(new Location(10.0, 10.0, "LT", "City"));
        user.setSearchParameters(searchParameters);
        doReturn(match).when(defaultMatchFinder).findOne(any(MatchRequest.class));
        when(userService.loadUserById(1L)).thenReturn(user);
        Match resultMatch = matchService.nextFor(1L);
        verify(userService, times(1)).loadUserById(1L);
        verify(repositoryMatchFinder, times(1)).findOne(any(MatchRequest.class));
        verify(matchRepository, times(1)).save(match);
        verify(matchRequestValidator, times(1)).validateMatchRequest(user, searchParameters);
        assertEquals(match, resultMatch);
    }

    @Test
    public void testNextFor_validationException() throws Exception {
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        user.setSearchParameters(searchParameters);
        doThrow(new MatchRequestException("Error")).when(matchRequestValidator).validateMatchRequest(user, searchParameters);
        when(userService.loadUserById(1L)).thenReturn(user);
        try {
            matchService.nextFor(1L);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Error", mre.getMessage());
            verify(userService, times(1)).loadUserById(1L);
            verify(repositoryMatchFinder, times(0)).findOne(any(MatchRequest.class));
            verify(defaultMatchFinder, times(0)).findOne(any(MatchRequest.class));
            verify(matchRepository, times(0)).save(any(Match.class));
            verify(matchRequestValidator, times(1)).validateMatchRequest(user, searchParameters);
        }
    }

    @Test
    public void testResponseByUser() throws Exception {
        MatchParty matchParty1 = createMatchParty(1L, createUser(1L), MatchParty.Response.UNDEFINED);
        MatchParty matchParty2 = createMatchParty(2L, createUser(2L), MatchParty.Response.UNDEFINED);
        Match match = MatchTestUtils.createMatch(1L, matchParty1, matchParty2);
        when(matchRepository.findOne(1L)).thenReturn(match);
        matchService.responseByUser(1L, MatchParty.Response.YES, 2L);
        verify(matchRepository, times(1)).save(match);
        assertEquals(MatchParty.Response.YES, match.getMatchParty(2L).getResponse());
        assertEquals(MatchParty.Response.UNDEFINED, match.getMatchParty(1L).getResponse());
    }

    @Test
    public void testResponseByUser_invalidUser() throws Exception {
        MatchParty matchParty1 = createMatchParty(1L, createUser(1L), MatchParty.Response.UNDEFINED);
        MatchParty matchParty2 = createMatchParty(2L, createUser(2L), MatchParty.Response.UNDEFINED);
        Match match = MatchTestUtils.createMatch(1L, matchParty1, matchParty2);
        when(matchRepository.findOne(1L)).thenReturn(match);
        try {
            matchService.responseByUser(1L, MatchParty.Response.YES, 3L);
            fail();
        } catch (ForbiddenException fe) {
            assertEquals("Invalid user", fe.getMessage());
        }
        verify(matchRepository, never()).save(match);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResponseByUser_matchNotFound() throws Exception {
        when(matchRepository.findOne(1L)).thenReturn(null);
        matchService.responseByUser(1L, MatchParty.Response.YES, 2L);
    }

    @Test
    public void testGetUserMutualMatches_noMatches() throws Exception {
        User user = new User();
        when(userService.loadUserById(1L)).thenReturn(user);
        when(matchRepository.findByUser(user, MatchParty.Response.YES)).thenReturn(new HashSet<>());
        Set<Match> matches = matchService.getUserMutualMatches(1L);
        assertNotNull(matches);
        assertEquals(0, matches.size());
    }

    @Test
    public void testGetUserMutualMatches() throws Exception {
        User user = createUser(100L);
        Set<Match> matches = new HashSet<>();
        long i = 1;
        for (; i<=3; i++) {
            MatchParty matchParty1 = createMatchParty(i, user, MatchParty.Response.YES);
            MatchParty matchParty2 = createMatchParty(i, createUser(i), MatchParty.Response.YES);
            matches.add(MatchTestUtils.createMatch(i, matchParty1, matchParty2));
        }
        for (; i<=6; i++) {
            MatchParty matchParty1 = createMatchParty(i, user, MatchParty.Response.YES);
            MatchParty matchParty2 = createMatchParty(i, createUser(i), MatchParty.Response.NO);
            matches.add(MatchTestUtils.createMatch(i, matchParty1, matchParty2));
        }
        for (; i<=9; i++) {
            MatchParty matchParty1 = createMatchParty(i, user, MatchParty.Response.YES);
            MatchParty matchParty2 = createMatchParty(i, createUser(i), MatchParty.Response.UNDEFINED);
            matches.add(MatchTestUtils.createMatch(i, matchParty1, matchParty2));
        }
        when(userService.loadUserById(100L)).thenReturn(user);
        when(matchRepository.findByUser(user, MatchParty.Response.YES)).thenReturn(matches);
        Set<Match> result = matchService.getUserMutualMatches(100L);
        assertEquals(3, result.size());
        Iterator<Match> matchIterator = result.iterator();
        while (matchIterator.hasNext()) {
            Match match = matchIterator.next();
            assertEquals(MatchParty.Response.YES, match.getMatchParty(100L).getResponse());
            assertEquals(MatchParty.Response.YES, match.getOppositeMatchParty(100L).getResponse());
        }
    }

    @Test
    public void testGetUserMatch() throws Exception {
        Match match = new Match();
        when(matchRepository.findOneByUser(1L, 2L)).thenReturn(Optional.of(match));
        Match result = matchService.getUserMatch(1L, 2L);
        assertEquals(result, match);
    }

}