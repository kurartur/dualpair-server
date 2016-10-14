package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.match.suitability.SuitabilityVerifier;
import lt.dualpair.server.domain.model.match.suitability.VerificationContext;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class RepositoryMatchFinder implements MatchFinder {

    private MatchRepository matchRepository;
    private SuitabilityVerifier suitabilityVerifier;

    @Override
    public Match findOne(MatchRequest matchRequest) {
        List<Long> excluded = matchRequest.getExcludedOpponentIds();
        Set<Match> matches = matchRepository.findNotReviewed(matchRequest.getUser(), excluded.isEmpty() ? Arrays.asList(-1L) : excluded);
        if (matches.isEmpty()) {
            return null;
        }
        Long userId = matchRequest.getUser().getId();
        for (Match match : matches) {
            if (suitabilityVerifier.verify(new VerificationContext(match.getMatchParty(userId).getUser()),
                    new VerificationContext(match.getOppositeMatchParty(userId).getUser()))) {
                return match;
            } else {
                matchRepository.delete(match);
            }
        }
        return null;
    }

    @Autowired
    public void setMatchRepository(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Autowired
    public void setSuitabilityVerifier(SuitabilityVerifier suitabilityVerifier) {
        this.suitabilityVerifier = suitabilityVerifier;
    }
}
