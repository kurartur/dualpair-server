package com.artur.dualpair.server.domain.model.match;

import com.artur.dualpair.server.domain.model.geo.DistanceCalculator;
import com.artur.dualpair.server.domain.model.geo.Location;
import com.artur.dualpair.server.domain.model.socionics.RelationType;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.persistence.repository.SociotypeRepository;
import com.artur.dualpair.server.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DefaultMatchFinder implements MatchFinder {

    private static final double MAXIMUM_DISTANCE = 300e3;

    private UserRepository userRepository;
    private SociotypeRepository sociotypeRepository;
    private DistanceCalculator distanceCalculator;

    @Override
    public Match findFor(User user, SearchParameters searchParameters) {
        Sociotype dualSociotype = sociotypeRepository.findOppositeByRelationType(user.getRandomSociotype().getCode1(), RelationType.Code.DUAL);
        Set<User> opponents = userRepository.findOpponent(user, dualSociotype, searchParameters);
        if (opponents.size() != 0) {
            ClosestOpponentResult closestOpponent = findClosestOpponent(user.getSearchParameters().getLocation(), opponents);
            if (closestOpponent == null) {
                return null;
            }
            return createMatch(user, closestOpponent.opponent, new Double(closestOpponent.distance).intValue());
        }
        return null;
    }

    private ClosestOpponentResult findClosestOpponent(Location userLocation, Set<User> opponents) {
        double userLatitude = userLocation.getLatitude();
        double userLongitude = userLocation.getLongitude();
        double shortest = Double.MAX_VALUE;
        User closestOpponent = null;
        for (User opponent : opponents) {
            Location opponentLocation = opponent.getSearchParameters().getLocation();
            double distance = distanceCalculator.calculate(userLatitude, userLongitude, opponentLocation.getLatitude(), opponentLocation.getLongitude());
            if (distance <= MAXIMUM_DISTANCE && shortest > distance) {
                shortest = distance;
                closestOpponent = opponent;
            }
        }
        return closestOpponent == null ? null : new ClosestOpponentResult(closestOpponent, shortest);
    }

    private Match createMatch(User user, User opponent, Integer distance) {
        Match match = new Match();
        match.setUser(user);
        match.setOpponent(opponent);
        match.setDistance(distance);
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

    private static final class ClosestOpponentResult {

        protected User opponent;
        protected double distance;

        public ClosestOpponentResult(User opponent, double distance) {
            this.opponent = opponent;
            this.distance = distance;
        }
    }
}
