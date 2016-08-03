package lt.dualpair.server.domain.model.match;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RepositoryMatchFinderTest {

    private RepositoryMatchFinder repositoryMatchFinder = new RepositoryMatchFinder();
    private MatchRepository matchRepository = mock(MatchRepository.class);

    @Before
    public void setUp() throws Exception {
        repositoryMatchFinder.setMatchRepository(matchRepository);
    }

    @Test
    public void testFindOne() throws Exception {
        User user = UserTestUtils.createUser();
        List<Long> exclude = Arrays.asList(1L);
        Set<Match> matchSet = new HashSet<>();
        Match match = new Match();
        matchSet.add(match);
        when(matchRepository.findNotReviewed(user, exclude)).thenReturn(matchSet);
        Match resultMatch = repositoryMatchFinder.findOne(new MatchRequestBuilder(user).excludeOpponents(exclude).build());
        assertEquals(match, resultMatch);
    }

}