package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.match.suitability.SuitabilityVerifier;
import lt.dualpair.server.domain.model.match.suitability.VerificationContext;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RepositoryMatchFinderTest {

    private RepositoryMatchFinder repositoryMatchFinder = new RepositoryMatchFinder();
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private SuitabilityVerifier suitabilityVerifier = mock(SuitabilityVerifier.class);

    @Before
    public void setUp() throws Exception {
        repositoryMatchFinder.setMatchRepository(matchRepository);
        repositoryMatchFinder.setSuitabilityVerifier(suitabilityVerifier);
    }

    @Test
    public void testFindOne() throws Exception {
        User user = UserTestUtils.createUser();
        List<Long> exclude = Arrays.asList(1L);
        Set<Match> matchSet = new HashSet<>();
        Match match = MatchTestUtils.createMatch(1L,
                MatchPartyTestUtils.createMatchParty(1L, user, Response.UNDEFINED),
                MatchPartyTestUtils.createMatchParty(2L, UserTestUtils.createUser(2L), Response.UNDEFINED));
        matchSet.add(match);
        when(matchRepository.findNotReviewed(user, exclude)).thenReturn(matchSet);
        when(suitabilityVerifier.verify(any(VerificationContext.class), any(VerificationContext.class))).thenReturn(true);
        Match resultMatch = repositoryMatchFinder.findOne(new MatchRequestBuilder(user).excludeOpponents(exclude).build());
        assertEquals(match, resultMatch);
    }

    @Test
    public void testFindOne_notSuitable() throws Exception {
        User user = UserTestUtils.createUser();
        Set<Match> matchSet = new HashSet<>();
        Match match = MatchTestUtils.createMatch(1L,
                MatchPartyTestUtils.createMatchParty(1L, user, Response.UNDEFINED),
                MatchPartyTestUtils.createMatchParty(2L, UserTestUtils.createUser(2L), Response.UNDEFINED));
        matchSet.add(match);
        when(matchRepository.findNotReviewed(user, Arrays.asList(-1L))).thenReturn(matchSet);
        when(suitabilityVerifier.verify(any(VerificationContext.class), any(VerificationContext.class))).thenReturn(false);
        Match resultMatch = repositoryMatchFinder.findOne(new MatchRequestBuilder(user).build());
        assertNull(resultMatch);
        verify(matchRepository, times(1)).delete(match);
    }
}