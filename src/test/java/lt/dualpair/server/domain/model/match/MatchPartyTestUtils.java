package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.user.User;

public class MatchPartyTestUtils {

    public static MatchParty createMatchParty(Long id, User user, Response response) {
        MatchParty matchParty = new MatchParty();
        matchParty.setId(id);
        matchParty.setUser(user);
        matchParty.setResponse(response);
        return matchParty;
    }

}
