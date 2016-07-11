package com.artur.dualpair.server.domain.model.match;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.persistence.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RepositoryMatchFinder implements MatchFinder {

    private MatchRepository matchRepository;

    @Override
    public Match findFor(User user, SearchParameters searchParameters) {
        Set<Match> matches = matchRepository.findByOpponent(user);
        if (matches.size() != 0) {
            return inverse(matches.iterator().next());
        }
        return null;
    }

    private Match inverse(Match match) {
        Match inversed = new Match();
        inversed.setUser(match.getOpponent());
        inversed.setOpponent(match.getUser());
        return inversed;
    }

    @Autowired
    public void setMatchRepository(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }
}
