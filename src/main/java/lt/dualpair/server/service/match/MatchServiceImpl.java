package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.match.*;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    private DefaultMatchFinder defaultMatchFinder;
    private RepositoryMatchFinder repositoryMatchFinder;
    private MatchRepository matchRepository;
    private UserService userService;
    private MatchRequestValidator matchRequestValidator;
    private NotificationSender notificationSender;

    @Override
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

    @Override
    @Transactional
    public void sendMutualMatchNotifications(Match match) {
        if (match.isMutual()) {
            for (MatchParty matchParty : match.getMatchParties()) {
                notificationSender.sendNotification(new Notification(matchParty.getUser().getId(), "New match"));
            }
        }
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

    @Autowired
    public void setNotificationSender(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

}
