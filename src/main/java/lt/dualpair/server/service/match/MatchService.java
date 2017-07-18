package lt.dualpair.server.service.match;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchRequest;
import lt.dualpair.core.match.MatchRequestException;

public interface MatchService {

    Match nextFor(MatchRequest matchRequest) throws MatchRequestException;

    void sendMutualMatchNotifications(Match match);

}
