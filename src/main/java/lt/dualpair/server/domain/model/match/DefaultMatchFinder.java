package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.geo.DistanceCalculator;
import lt.dualpair.server.domain.model.socionics.RelationType;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;
import lt.dualpair.server.infrastructure.persistence.repository.RelationTypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
public class DefaultMatchFinder implements MatchFinder {

    private UserRepository userRepository;
    private SociotypeRepository sociotypeRepository;
    private DistanceCalculator distanceCalculator;
    private RelationTypeRepository relationTypeRepository;

    @Override
    public Match findOne(MatchRequest matchRequest) {
        User user = matchRequest.getUser();
        RelationType.Code relationType = matchRequest.getRelationType();
        Sociotype dualSociotype = sociotypeRepository.findOppositeByRelationType(user.getRandomSociotype().getCode1(), relationType);
        Set<User> opponents = userRepository.findOpponents(new UserRepositoryImpl.FindOpponentsParams(
                user,
                dualSociotype,
                matchRequest.getMinAge(),
                matchRequest.getMaxAge(),
                matchRequest.getGenders(),
                matchRequest.getCountryCode(),
                matchRequest.getExcludedOpponentIds()
        ));
        if (opponents.size() != 0) {
            ClosestOpponentResult closestOpponent = findClosestOpponent(matchRequest.getLatitude(), matchRequest.getLongitude(), matchRequest.getRadius(), opponents);
            if (closestOpponent == null) {
                return null;
            }
            return createMatch(user, closestOpponent.opponent, relationType, new Double(closestOpponent.distance).intValue());
        }
        return null;
    }

    private ClosestOpponentResult findClosestOpponent(double userLatitude, double userLongitude, double radius, Set<User> opponents) {
        double shortest = Double.MAX_VALUE;
        User closestOpponent = null;
        for (User opponent : opponents) {
            UserLocation opponentLocation = opponent.getRecentLocation();
            double distance = distanceCalculator.calculate(userLatitude, userLongitude, opponentLocation.getLatitude(), opponentLocation.getLongitude());
            if (distance <= radius && shortest > distance) {
                shortest = distance;
                closestOpponent = opponent;
            }
        }
        return closestOpponent == null ? null : new ClosestOpponentResult(closestOpponent, shortest);
    }

    private Match createMatch(User user, User opponent, RelationType.Code relationType, Integer distance) {
        Match match = new Match();
        match.setRelationType(relationTypeRepository.findByCode(relationType).get());
        match.setMatchParties(new MatchParty(match, user), new MatchParty(match, opponent));
        match.setDistance(distance);
        match.setDateCreated(new Date());
        return match;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSociotypeRepository(SociotypeRepository sociotypeRepository) {
        this.sociotypeRepository = sociotypeRepository;
    }

    @Autowired
    public void setDistanceCalculator(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Autowired
    public void setRelationTypeRepository(RelationTypeRepository relationTypeRepository) {
        this.relationTypeRepository = relationTypeRepository;
    }

    private static final class ClosestOpponentResult {

        protected User opponent;
        protected double distance;

        public ClosestOpponentResult(User opponent, double distance) {
            this.opponent = opponent;
            this.distance = distance;
        }
    }
}
