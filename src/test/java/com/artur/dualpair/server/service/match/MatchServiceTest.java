package com.artur.dualpair.server.service.match;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.match.DefaultMatchFinder;
import com.artur.dualpair.server.domain.model.match.RepositoryMatchFinder;
import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.persistence.repository.MatchRepository;
import com.artur.dualpair.server.service.user.UserService;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

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
        user.setSearchParameters(searchParameters);
        doReturn(match).when(repositoryMatchFinder).findFor(eq(user), eq(searchParameters));
        when(userService.loadUserByUserId("1")).thenReturn(user);
        Match resultMatch = matchService.nextFor("1");
        verify(userService, times(1)).loadUserByUserId("1");
        verify(repositoryMatchFinder, times(1)).findFor(user, searchParameters);
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
        user.setSearchParameters(searchParameters);
        doReturn(match).when(defaultMatchFinder).findFor(eq(user), eq(searchParameters));
        when(userService.loadUserByUserId("1")).thenReturn(user);
        Match resultMatch = matchService.nextFor("1");
        verify(userService, times(1)).loadUserByUserId("1");
        verify(repositoryMatchFinder, times(1)).findFor(user, searchParameters);
        verify(defaultMatchFinder, times(1)).findFor(user, searchParameters);
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
        when(userService.loadUserByUserId("1")).thenReturn(user);
        try {
            matchService.nextFor("1");
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Error", mre.getMessage());
            verify(userService, times(1)).loadUserByUserId("1");
            verify(repositoryMatchFinder, times(0)).findFor(user, searchParameters);
            verify(defaultMatchFinder, times(0)).findFor(user, searchParameters);
            verify(matchRepository, times(0)).save(any(Match.class));
            verify(matchRequestValidator, times(1)).validateMatchRequest(user, searchParameters);
        }
    }

    @Test
    public void testResponseByUser() throws Exception {
        Match match = createMatch(1L, "userId", Match.Response.UNDEFINED);
        when(matchRepository.findOne(1L)).thenReturn(match);
        matchService.responseByUser(1L, Match.Response.YES, "userId");
        verify(matchRepository, times(1)).save(match);
        assertEquals(Match.Response.YES, match.getResponse());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResponseByUser_matchNotFound() throws Exception {
        when(matchRepository.findOne(1L)).thenReturn(null);
        matchService.responseByUser(1L, Match.Response.YES, "userId");
    }

    @Test
    public void testResponseByUser_invalidUser() throws Exception {
        Match match = createMatch(1L, "userId", Match.Response.UNDEFINED);
        when(matchRepository.findOne(1L)).thenReturn(match);
        try {
            matchService.responseByUser(1L, Match.Response.YES, "otherUserId");
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid user", iae.getMessage());
        }
        verify(matchRepository, never()).save(match);
        assertEquals(Match.Response.UNDEFINED, match.getResponse());
    }

    @Test
    public void testGetUserMatch_invalidUser() throws Exception {
        User user = new User();
        user.setUsername("username");
        when(userService.loadUserByUsername("username")).thenReturn(user);
        when(matchRepository.findOne(1L)).thenReturn(createMatch(1L, "otherUser", Match.Response.YES));
        try {
            matchService.getUserMatch(1L, "username");
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid user", iae.getMessage());
        }
    }

    @Test
    public void testGetUserMatch() throws Exception {
        User user = new User();
        user.setUsername("username");
        Match match = createMatch(1L, "username", Match.Response.YES);
        when(userService.loadUserByUsername("username")).thenReturn(user);
        when(matchRepository.findOne(1L)).thenReturn(match);
        Match result = matchService.getUserMatch(1L, "username");
        assertEquals(result, match);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserMatch_matchNotFound() throws Exception {
        when(matchRepository.findOne(1L)).thenReturn(null);
        matchService.getUserMatch(1L, "userId");
    }

    @Test
    public void testGetUserMatches_noMatches() throws Exception {
        User user = new User();
        when(userService.loadUserByUsername("username")).thenReturn(user);
        when(matchRepository.findByUser(user)).thenReturn(new HashSet<>());
        Set<Match> matches = matchService.getUserMatches("username");
        assertNotNull(matches);
        assertEquals(0, matches.size());
    }

    @Test
    public void testGetUserMatches() throws Exception {
        User user = new User();
        Set<Match> matches = new HashSet<>();
        matches.add(createMatch(1L, "username", Match.Response.UNDEFINED));
        matches.add(createMatch(2L, "username", Match.Response.YES));
        matches.add(createMatch(3L, "username", Match.Response.NO));
        when(userService.loadUserByUsername("username")).thenReturn(user);
        when(matchRepository.findByUser(user)).thenReturn(matches);
        Set<Match> result = matchService.getUserMatches("username");
        assertEquals(3, result.size());
    }

    private Match createMatch(Long id, String userId, Match.Response response) {
        Match match = new Match();
        match.setId(id);
        User user = new User();
        user.setUsername(userId);
        match.setUser(user);
        match.setResponse(response);
        return match;
    }
}