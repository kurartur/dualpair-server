package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.geo.DistanceCalculator;
import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.socionics.RelationType;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
    public void testFindFor() throws Exception {
        User user = createUser(Sociotype.Code1.EII);
        User opponent = createUser(Sociotype.Code1.LSE);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        user.setSearchParameters(createSearchParameters(10, "LT"));
        opponent.setSearchParameters(createSearchParameters(12, "LT"));
        Set<User> opponents = new HashSet<>();
        opponents.add(opponent);
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponent(user, pairSociotype, user.getSearchParameters())).thenReturn(opponents);
        doReturn(300000.0).when(distanceCalculator).calculate(10, 10, 12, 12);
        Match resultMatch = defaultMatchFinder.findFor(user, user.getSearchParameters());
        assertNotNull(resultMatch);
        assertEquals(user, resultMatch.getUser());
        assertEquals(opponent, resultMatch.getOpponent());
        assertEquals((Integer)300000, resultMatch.getDistance());
    }

    @Test
    public void testFindFor_noMatches() throws Exception {
        User user = createUser(Sociotype.Code1.EII);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        SearchParameters searchParameters = new SearchParameters();
        Set<User> opponents = new HashSet<>();
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponent(user, pairSociotype, searchParameters)).thenReturn(opponents);
        assertNull(defaultMatchFinder.findFor(user, searchParameters));
    }

    @Test
    public void testFindFor_closest() throws Exception {
        User user = createUser(Sociotype.Code1.EII);
        user.setSearchParameters(createSearchParameters(10, "LT"));
        User opponent1 = createUser(Sociotype.Code1.LSE);
        User opponent2 = createUser(Sociotype.Code1.LSE);
        User opponent3 = createUser(Sociotype.Code1.LSE);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        opponent1.setSearchParameters(createSearchParameters(12, "LT"));
        opponent2.setSearchParameters(createSearchParameters(13, "LT"));
        opponent3.setSearchParameters(createSearchParameters(14, "LT"));
        Set<User> opponents = new LinkedHashSet<>();
        opponents.add(opponent1);
        opponents.add(opponent2);
        opponents.add(opponent3);
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponent(user, pairSociotype, user.getSearchParameters())).thenReturn(opponents);
        doReturn(300000.0).when(distanceCalculator).calculate(10, 10, 12, 12);
        doReturn(299999.7).when(distanceCalculator).calculate(10, 10, 13, 13);
        doReturn(299999.8).when(distanceCalculator).calculate(10, 10, 14, 14);
        Match resultMatch = defaultMatchFinder.findFor(user, user.getSearchParameters());
        assertNotNull(resultMatch);
        assertEquals(user, resultMatch.getUser());
        assertEquals(opponent2, resultMatch.getOpponent());
        assertEquals((Integer)299999, resultMatch.getDistance());
    }

    @Test
    public void testFindFor_tooFar() throws Exception {
        User user = createUser(Sociotype.Code1.EII);
        User opponent = createUser(Sociotype.Code1.LSE);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        user.setSearchParameters(createSearchParameters(10, "LT"));
        opponent.setSearchParameters(createSearchParameters(12, "LT"));
        Set<User> opponents = new HashSet<>();
        opponents.add(opponent);
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponent(user, pairSociotype, user.getSearchParameters())).thenReturn(opponents);
        doReturn(300000.1).when(distanceCalculator).calculate(10, 10, 12, 12);
        Match resultMatch = defaultMatchFinder.findFor(user, user.getSearchParameters());
        assertNull(resultMatch);
    }

    private User createUser(Sociotype.Code1 sociotype) {
        User user = new User();
        Set<Sociotype> sociotypes = new HashSet<>();
        sociotypes.add(new Sociotype.Builder().code1(sociotype).build());
        user.setSociotypes(sociotypes);
        return user;
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