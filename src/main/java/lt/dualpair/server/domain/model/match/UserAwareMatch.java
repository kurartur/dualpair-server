package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.user.User;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

public class UserAwareMatch {

    private Long userId;
    private Match match;

    public UserAwareMatch(User user, Match match) {
        Assert.notNull(user);
        Assert.notNull(match);
        this.userId = user.getId();
        this.match = match;
    }

    public UserAwareMatch(Long userId, Match match) {
        Assert.notNull(userId);
        Assert.notNull(match);
        this.userId = userId;
        this.match = match;
    }

    public Long getId() {
        return match.getId();
    }

    public MatchParty getUserMatchParty() {
        return match.getMatchParty(userId);
    }

    public MatchParty getOpponentMatchParty() {
        return match.getOppositeMatchParty(userId);
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

        if (!userId.equals(that.userId)) return false;
        return match.equals(that.match);

    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + match.hashCode();
        return result;
    }
}
