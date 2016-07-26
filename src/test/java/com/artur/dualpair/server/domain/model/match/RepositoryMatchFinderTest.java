package com.artur.dualpair.server.domain.model.match;

import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class RepositoryMatchFinderTest {

    private RepositoryMatchFinder repositoryMatchFinder = new RepositoryMatchFinder();
    private MatchRepository matchRepository = mock(MatchRepository.class);

    @Before
    public void setUp() throws Exception {
        repositoryMatchFinder.setMatchRepository(matchRepository);
    }

    @Test
    public void testFindFor() throws Exception {
        User user = createUser();
        User opponent = createUser();
        SearchParameters searchParameters = createSearchParameters();
        Match match = createMatch(opponent, user);
        Set<Match> matchSet = new HashSet<>();
        matchSet.add(match);
        when(matchRepository.findByOpponent(user)).thenReturn(matchSet);
        Match resultMatch = repositoryMatchFinder.findFor(user, searchParameters);
        verify(matchRepository, times(1)).findByOpponent(user);
        assertEquals(user, resultMatch.getUser());
        assertEquals(opponent, resultMatch.getOpponent());
    }

    @Test
     public void testFindFor_noMatches() throws Exception {
        User user = createUser();
        SearchParameters searchParameters = createSearchParameters();
        Set<Match> matchSet = new HashSet<>();
        when(matchRepository.findByOpponent(user)).thenReturn(matchSet);
        assertNull(repositoryMatchFinder.findFor(user, searchParameters));
        verify(matchRepository, times(1)).findByOpponent(user);
    }

    private SearchParameters createSearchParameters() {
        SearchParameters searchParameters = new SearchParameters();
        return searchParameters;
    }

    private User createUser() {
        User user = new User();
        return user;
    }

    private Match createMatch(User user, User opponent) {
        Match match = new Match();
        match.setUser(user);
        match.setOpponent(opponent);
        return match;
    }
}