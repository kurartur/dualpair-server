package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.match.*;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.interfaces.web.controller.rest.ForbiddenException;
import lt.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
public class MatchService {

    private DefaultMatchFinder defaultMatchFinder;
    private RepositoryMatchFinder repositoryMatchFinder;
    private MatchRepository matchRepository;
    private UserService userService;
    private MatchRequestValidator matchRequestValidator;

    @Transactional
    public Match nextFor(Long userId, List<Long> excludeOpponents) throws MatchRequestException {
        User user = userService.loadUserById(userId);
        matchRequestValidator.validateMatchRequest(user, user.getSearchParameters());
        MatchRequestBuilder builder =  MatchRequestBuilder.findFor(user)
                .apply(user.getSearchParameters());
        if (excludeOpponents != null) {
            builder.excludeOpponents(excludeOpponents);
        }
        MatchRequest matchRequest = builder.build();
        Match match = repositoryMatchFinder.findOne(matchRequest);
        if (match == null) {
            match = defaultMatchFinder.findOne(matchRequest);
        }
        if (match != null) {
            matchRepository.save(match);
        }
        return match;
    }

    public Match nextFor(Long userId) throws MatchRequestException {
        return nextFor(userId, null);
    }

    @Transactional
    public void responseByUser(Long matchId, Match.Response response, String userId) {
        Match match = findNotNullMatch(matchId);
        if (!match.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("Invalid user");
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

    /*
     *   @see getUserMatch(Long matchId, Long userId)
     */
    @Deprecated
    public Match getUserMatch(Long matchId, String username) {
        Match match = findNotNullMatch(matchId);
        if (!match.getUser().getUsername().equals(username)) {
            throw new ForbiddenException("Invalid user");
        }
        return match;
    }

    public Match getUserMatch(Long matchId, Long userId) {
        Match match = findNotNullMatch(matchId);
        if (!match.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Invalid user");
        }
        return match;
    }

    public Set<Match> getUserMatches(String username) {
        User user = userService.loadUserByUsername(username);
        return matchRepository.findByUser(user);
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
