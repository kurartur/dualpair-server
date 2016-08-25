package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.user.User;

public class UserAwareMatch {

    private User user;
    private Match match;

    public UserAwareMatch(User user, Match match) {
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
}
