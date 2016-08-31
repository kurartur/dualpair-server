package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.match.*;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.interfaces.web.controller.rest.ForbiddenException;
import lt.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private DefaultMatchFinder defaultMatchFinder;
    private RepositoryMatchFinder repositoryMatchFinder;
    private MatchRepository matchRepository;
    private UserService userService;
    private MatchRequestValidator matchRequestValidator;
    private NotificationSender notificationSender;

    @Transactional
    public Match nextFor(Long userId, List<Long> excludeOpponents) throws MatchRequestException {
        User user = userService.loadUserById(userId);
        matchRequestValidator.validateMatchRequest(user, user.getSearchParameters());
        MatchRequestBuilder builder =  MatchRequestBuilder.findFor(user)
                .apply(user.getSearchParameters())
                .location(user.getRecentLocation().getLatitude(), user.getRecentLocation().getLongitude(), user.getRecentLocation().getCountryCode());
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
    public void responseByUser(Long matchId, Response response, Long userId) {
        Match match = findNotNullMatch(matchId);
        MatchParty matchParty = match.getMatchParty(userId);
        if (matchParty == null) {
            throw new ForbiddenException("Invalid user");
        }
        matchParty.setResponse(response);
        matchRepository.save(match);

        if (match.isMutual()) {
            notificationSender.sendNotification(new Notification(matchParty.getUser().getId(), "New match"));
            notificationSender.sendNotification(new Notification(match.getOppositeMatchParty(userId).getUser().getId(), "New match"));
        }
    }

    private Match findNotNullMatch(Long matchId) {
        Match match = matchRepository.findOne(matchId);
        if (match == null)
            throw new IllegalArgumentException("Match #" + matchId + " not found");
        return match;
    }

    public Match getUserMatch(Long matchId, Long userId) {
        Optional<Match> match = matchRepository.findOneByUser(matchId, userId);
        return match.isPresent() ? match.get() : null;
    }

    public Set<Match> getUserMutualMatches(final Long userId) {
        User user = userService.loadUserById(userId);
        Set<Match> matches =  matchRepository.findByUser(user, Response.YES);
        return matches.stream()
                .filter(match -> match.getOppositeMatchParty(userId).getResponse() == Response.YES)
                .collect(Collectors.toSet());
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

    public void setNotificationSender(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }
}
