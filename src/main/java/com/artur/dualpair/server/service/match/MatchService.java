package com.artur.dualpair.server.service.match;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.match.DefaultMatchFinder;
import com.artur.dualpair.server.domain.model.match.RepositoryMatchFinder;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.persistence.repository.MatchRepository;
import com.artur.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class MatchService {

    private DefaultMatchFinder defaultMatchFinder;
    private RepositoryMatchFinder repositoryMatchFinder;
    private MatchRepository matchRepository;
    private UserService userService;
    private MatchRequestValidator matchRequestValidator;

    @Transactional
    public Match nextFor(String userId) throws MatchRequestException {
        User user = userService.loadUserByUserId(userId);
        matchRequestValidator.validateMatchRequest(user, user.getSearchParameters());
        Match match = repositoryMatchFinder.findFor(user, user.getSearchParameters());
        if (match == null) {
            match = defaultMatchFinder.findFor(user, user.getSearchParameters());
        }
        if (match != null) {
            matchRepository.save(match);
        }
        return match;
    }

    @Transactional
    public void responseByUser(Long matchId, Match.Response response, String userId) {
        Match match = findNotNullMatch(matchId);
        if (!match.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Invalid user");
        }
        match.setResponse(response);
        matchRepository.save(match);
    }

    private Match findNotNullMatch(Long matchId) {
        Match match = matchRepository.findOne(matchId);
        if (match == null)
            throw new IllegalArgumentException("Match #" + matchId + " not found");
        return match;
    }

    @Autowired
    public void setDefaultMatchFinder(DefaultMatchFinder defaultMatchFinder) {
        this.defaultMatchFinder = defaultMatchFinder;
    }

    @Autowired
    public void setRepositoryMatchFinder(RepositoryMatchFinder repositoryMatchFinder) {
        this.repositoryMatchFinder = repositoryMatchFinder;
    }

    @Autowired
    public void setMatchRepository(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMatchRequestValidator(MatchRequestValidator matchRequestValidator) {
        this.matchRequestValidator = matchRequestValidator;
    }
}
