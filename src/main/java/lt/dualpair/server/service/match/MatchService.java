package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchRequest;
import lt.dualpair.server.domain.model.match.MatchRequestException;

public interface MatchService {

    Match nextFor(MatchRequest matchRequest) throws MatchRequestException;

    void sendMutualMatchNotifications(Match match);

}
