package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchRequestException;

import java.util.List;

public interface MatchService {

    Match nextFor(Long userId, List<Long> excludeOpponents) throws MatchRequestException;

    void sendMutualMatchNotifications(Match match);

}
