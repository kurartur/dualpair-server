package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.geo.DistanceCalculator;
import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.socionics.RelationType;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepositoryImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static lt.dualpair.server.domain.model.user.UserTestUtils.createUser;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultMatchFinderTest {

    private DefaultMatchFinder defaultMatchFinder = new DefaultMatchFinder();
    private UserRepository userRepository = mock(UserRepository.class);
    private SociotypeRepository sociotypeRepository = mock(SociotypeRepository.class);
    private DistanceCalculator distanceCalculator = mock(DistanceCalculator.class);

    @Before
    public void setUp() throws Exception {
        defaultMatchFinder.setUserRepository(userRepository);
        defaultMatchFinder.setSociotypeRepository(sociotypeRepository);
        defaultMatchFinder.setDistanceCalculator(distanceCalculator);
    }

    @Test
    public void testFindOne() throws Exception {
        User user = createUser(1L, Sociotype.Code1.EII);
        User opponent = createUser(2L, Sociotype.Code1.LSE);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        opponent.setSearchParameters(createSearchParameters(12, "LT"));
        Set<User> opponents = new HashSet<>(Collections.singletonList(opponent));
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponents(any(UserRepositoryImpl.FindOpponentsParams.class))).thenReturn(opponents);
        doReturn(300000.0).when(distanceCalculator).calculate(10, 10, 12, 12);
        Match resultMatch = defaultMatchFinder.findOne(new MatchRequestBuilder(user).location(10, 10, "LT").build());
        assertNotNull(resultMatch);
        assertEquals(user, resultMatch.getMatchParty(1L).getUser());
        assertEquals(opponent, resultMatch.getOppositeMatchParty(1L).getUser());
        assertEquals((Integer)300000, resultMatch.getDistance());
    }

    @Test
    public void testFindOne_tooFar() throws Exception {
        User user = createUser(1L, Sociotype.Code1.EII);
        User opponent = createUser(2L, Sociotype.Code1.LSE);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        opponent.setSearchParameters(createSearchParameters(12, "LT"));
        Set<User> opponents = new HashSet<>(Collections.singletonList(opponent));
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponents(any(UserRepositoryImpl.FindOpponentsParams.class))).thenReturn(opponents);
        doReturn(300000.1).when(distanceCalculator).calculate(10, 10, 12, 12);
        Match resultMatch = defaultMatchFinder.findOne(new MatchRequestBuilder(user).location(10, 10, "LT").build());
        assertNull(resultMatch);
    }



    @Test
    public void testFindOne_closest() throws Exception {
        User user = createUser(1L, Sociotype.Code1.EII);
        User opponent1 = createUser(2L, Sociotype.Code1.LSE);
        User opponent2 = createUser(3L, Sociotype.Code1.LSE);
        User opponent3 = createUser(4L, Sociotype.Code1.LSE);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        opponent1.setSearchParameters(createSearchParameters(12, "LT"));
        opponent2.setSearchParameters(createSearchParameters(13, "LT"));
        opponent3.setSearchParameters(createSearchParameters(14, "LT"));
        Set<User> opponents = new LinkedHashSet<>();
        opponents.add(opponent1);
        opponents.add(opponent2);
        opponents.add(opponent3);
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponents(any(UserRepositoryImpl.FindOpponentsParams.class))).thenReturn(opponents);
        doReturn(300000.0).when(distanceCalculator).calculate(10, 10, 12, 12);
        doReturn(299999.7).when(distanceCalculator).calculate(10, 10, 13, 13);
        doReturn(299999.8).when(distanceCalculator).calculate(10, 10, 14, 14);
        Match resultMatch = defaultMatchFinder.findOne(new MatchRequestBuilder(user).location(10, 10, "LT").build());
        assertNotNull(resultMatch);
        assertEquals(user, resultMatch.getMatchParty(1L).getUser());
        assertEquals(opponent2, resultMatch.getOppositeMatchParty(1L).getUser());
        assertEquals((Integer)299999, resultMatch.getDistance());
    }

    @Test
    public void testFindOnee_noMatches() throws Exception {
        User user = createUser(1L, Sociotype.Code1.EII);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        SearchParameters searchParameters = new SearchParameters();
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponents(any(UserRepositoryImpl.FindOpponentsParams.class))).thenReturn(new HashSet<>());
        assertNull(defaultMatchFinder.findOne(new MatchRequestBuilder(user).build()));
    }

    private SearchParameters createSearchParameters(double latLon, String country) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setLocation(new Location(latLon, latLon, country, "city"));
        return searchParameters;
    }

    private Sociotype createSociotype(Sociotype.Code1 code1) {
        return new Sociotype.Builder().code1(code1).build();
    }
}