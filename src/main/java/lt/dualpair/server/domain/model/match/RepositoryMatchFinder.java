package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class RepositoryMatchFinder implements MatchFinder {

    private MatchRepository matchRepository;

    @Override
    public Match findOne(MatchRequest matchRequest) {
        List<Long> excluded = matchRequest.getExcludedOpponentIds();
        Set<Match> matches = matchRepository.findNotReviewed(matchRequest.getUser(), excluded.isEmpty() ? Arrays.asList(-1L) : excluded);
        if (matches.isEmpty()) {
            return null;
        }
        return matches.iterator().next();
    }

    @Autowired
    public void setMatchRepository(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }
}
