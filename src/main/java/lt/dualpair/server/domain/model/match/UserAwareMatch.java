package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.user.User;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

public class UserAwareMatch {

    private User user;
    private Match match;

    public UserAwareMatch(User user, Match match) {
        Assert.notNull(user);
        Assert.notNull(match);
        this.user = user;
        this.match = match;
    }

    public Long getId() {
        return match.getId();
    }

    public MatchParty getUserMatchParty() {
        return match.getMatchParty(user.getId());
    }

    public MatchParty getOpponentMatchParty() {
        return match.getOppositeMatchParty(user.getId());
    }

    public Integer getDistance() {
        return match.getDistance();
    }

    public static Set<UserAwareMatch> fromSet(User user, Set<Match> matches) {
        Assert.notNull(user);
        Assert.notNull(matches);
        Set<UserAwareMatch> userAwareMatches = new HashSet<>();
        for (Match match : matches) {
            userAwareMatches.add(new UserAwareMatch(user, match));
        }
        return userAwareMatches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAwareMatch that = (UserAwareMatch) o;

        if (!user.equals(that.user)) return false;
        return match.equals(that.match);

    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + match.hashCode();
        return result;
    }
}
