package lt.dualpair.server.service.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchParty;
import lt.dualpair.core.user.*;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import lt.dualpair.server.infrastructure.notification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserResponseServiceImpl implements UserResponseService {

    private UserRepository userRepository;
    private UserResponseRepository userResponseRepository;
    private MatchRepository matchRepository;
    private NotificationSender notificationSender;

    public UserResponseServiceImpl(UserRepository userRepository,
                                   UserResponseRepository userResponseRepository,
                                   MatchRepository matchRepository,
                                   NotificationSender notificationSender) {
        this.userRepository = userRepository;
        this.userResponseRepository = userResponseRepository;
        this.matchRepository = matchRepository;
        this.notificationSender = notificationSender;
    }

    @Override
    @Transactional
    public void respond(Long userId, Long toUserId, Response response) {
        Assert.notNull(userId);
        Assert.notNull(toUserId);
        Assert.notNull(response);

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User " + userId + " not found"));
        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new IllegalArgumentException("User " + toUserId + " not found"));

        UserResponse userResponse;
        Optional<UserResponse> existingResponse = userResponseRepository.findByParties(userId, toUserId);
        if (existingResponse.isPresent()) {
            userResponse = existingResponse.get();
            if (userResponse.getMatch() != null) {
                throw new IllegalStateException("Response from user " + userId + " to user " + toUserId + " already exists and is match");
            }
        } else {
            userResponse = new UserResponse();
            userResponse.setUser(user);
            userResponse.setToUser(toUser);
        }

        userResponse.setDate(new Date());
        userResponse.setResponse(response);

        Optional<UserResponse> opponentResponseOpt = userResponseRepository.findByParties(toUserId, userId);
        if (opponentResponseOpt.isPresent()) {
            UserResponse opponentResponse = opponentResponseOpt.get();
            if (opponentResponse.getResponse() == Response.YES && response == Response.YES) {
                Match match = new Match();
                match.setMatchParties(new MatchParty(match, user), new MatchParty(match, toUser));
                match.setDate(new Date());
                matchRepository.save(match);
                sendMutualMatchNotifications(match);
                userResponse.setMatch(match);
                opponentResponse.setMatch(match);
                userResponseRepository.save(opponentResponse);
            }
        }

        userResponseRepository.save(userResponse);
    }

    @Override
    public Page<UserResponse> getResponsesPage(Long userId, Pageable pageable) {
        return userResponseRepository.fetchPageByUser(userId, pageable);
    }

    private void sendMutualMatchNotifications(Match match) {
        Long matchId = match.getId();
        for (MatchParty matchParty : match.getMatchParties()) {
            Long userId = matchParty.getUser().getId();
            User opponent = match.getOppositeMatchParty(userId).getUser();
            Notification<Map> notification = new Notification<>(userId,
                    NotificationType.NEW_MATCH,
                    createPayload(matchId, opponent.getId(), opponent.getName()));
            notificationSender.sendNotification(notification);
        }
    }

    private Map createPayload(Long id, Long userId, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("matchId", id);
        map.put("userId", userId);
        map.put("opponentName", name);
        return map;
    }

}
