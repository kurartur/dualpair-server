package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.geo.DistanceCalculator;
import lt.dualpair.server.domain.model.socionics.RelationType;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocationTestUtils;
import lt.dualpair.server.infrastructure.persistence.repository.RelationTypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepositoryImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static lt.dualpair.server.domain.model.user.UserTestUtils.createUser;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultMatchFinderTest {

    private DefaultMatchFinder defaultMatchFinder = new DefaultMatchFinder();
    private UserRepository userRepository = mock(UserRepository.class);
    private SociotypeRepository sociotypeRepository = mock(SociotypeRepository.class);
    private DistanceCalculator distanceCalculator = mock(DistanceCalculator.class);
    private RelationTypeRepository relationTypeRepository = mock(RelationTypeRepository.class);

    @Before
    public void setUp() throws Exception {
        defaultMatchFinder.setUserRepository(userRepository);
        defaultMatchFinder.setSociotypeRepository(sociotypeRepository);
        defaultMatchFinder.setDistanceCalculator(distanceCalculator);
        defaultMatchFinder.setRelationTypeRepository(relationTypeRepository);
        RelationType relationType = new RelationType.Builder().id(1).code(RelationType.Code.DUAL).build();
        when(relationTypeRepository.findByCode(RelationType.Code.DUAL)).thenReturn(Optional.of(relationType));
    }

    @Test
    public void testFindOne() throws Exception {
        User user = createUser(1L, Sociotype.Code1.EII);
        User opponent = createUser(2L, Sociotype.Code1.LSE);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        opponent.addLocation(UserLocationTestUtils.createUserLocation(12, "LT"), 1);
        Set<User> opponents = new HashSet<>(Collections.singletonList(opponent));
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.EII, RelationType.Code.DUAL)).thenReturn(pairSociotype);
        when(userRepository.findOpponents(any(UserRepositoryImpl.FindOpponentsParams.class))).thenReturn(opponents);
        doReturn(300000.0).when(distanceCalculator).calculate(10, 10, 12, 12);
        Match resultMatch = defaultMatchFinder.findOne(new MatchRequestBuilder(user).location(10, 10, "LT").build());
        assertNotNull(resultMatch);
        assertEquals(user, resultMatch.getMatchParty(1L).getUser());
        assertEquals(opponent, resultMatch.getOppositeMatchParty(1L).getUser());
        assertEquals((Integer) 300000, resultMatch.getDistance());
        assertEquals(RelationType.Code.DUAL, resultMatch.getRelationType().getCode());
    }

    @Test
    public void testFindOne_tooFar() throws Exception {
        User user = createUser(1L, Sociotype.Code1.EII);
        User opponent = createUser(2L, Sociotype.Code1.LSE);
        Sociotype pairSociotype = createSociotype(Sociotype.Code1.LSE);
        opponent.addLocation(UserLocationTestUtils.createUserLocation(12, "LT"), 1);
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
        opponent1.addLocation(UserLocationTestUtils.createUserLocation(12, "LT"), 5);
        opponent2.addLocation(UserLocationTestUtils.createUserLocation(13, "LT"), 5);
        opponent3.addLocation(UserLocationTestUtils.createUserLocation(14, "LT"), 5);
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

    private Sociotype createSociotype(Sociotype.Code1 code1) {
        return new Sociotype.Builder().code1(code1).build();
    }
}