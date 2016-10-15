package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.user.UserTestUtils;

public class MatchTestUtils {

    public static Match createMatch() {
        Match match = new Match();
        match.setId(1L);
        match.setMatchParties(MatchPartyTestUtils.createMatchParty(1L, UserTestUtils.createUser(1L), Response.UNDEFINED),
                MatchPartyTestUtils.createMatchParty(2L, UserTestUtils.createUser(2L), Response.UNDEFINED));
        return match;
    }

    public static Match createMatch(Long id, MatchParty party1, MatchParty party2) {
        Match match = new Match();
        match.setId(id);
        match.setMatchParties(party1, party2);
        party1.setMatch(match);
        party2.setMatch(match);
        return match;
    }

}
