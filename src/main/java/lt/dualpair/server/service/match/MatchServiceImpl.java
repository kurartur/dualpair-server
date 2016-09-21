package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.match.*;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import lt.dualpair.server.infrastructure.notification.NotificationType;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class MatchServiceImpl implements MatchService {

    private DefaultMatchFinder defaultMatchFinder;
    private RepositoryMatchFinder repositoryMatchFinder;
    private MatchRepository matchRepository;
    private UserService userService;
    private NotificationSender notificationSender;

    @Override
    @Transactional
    public Match nextFor(MatchRequest matchRequest) throws MatchRequestException {
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
            Long matchId = match.getId();
            for (MatchParty matchParty : match.getMatchParties()) {
                Long userId = matchParty.getUser().getId();
                User opponent = match.getOppositeMatchParty(userId).getUser();
                Notification<Map> notification = new Notification<>(userId,
                        NotificationType.NEW_MATCH,
                        createPayload(matchId, opponent.getName()));
                notificationSender.sendNotification(notification);
            }
        }
    }

    private Map createPayload(Long id, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("matchId", id);
        map.put("opponentName", name);
        return map;
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
    public void setNotificationSender(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

}
