package lt.dualpair.server.domain.model.match;

public class MatchTestUtils {

    public static Match createMatch(Long id, MatchParty party1, MatchParty party2) {
        Match match = new Match();
        match.setId(id);
        match.setMatchParties(party1, party2);
        party1.setMatch(match);
        party2.setMatch(match);
        return match;
    }

}
