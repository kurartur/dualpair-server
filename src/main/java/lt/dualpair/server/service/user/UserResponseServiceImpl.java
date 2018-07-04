package lt.dualpair.server.service.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchParty;
import lt.dualpair.core.user.*;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import lt.dualpair.server.infrastructure.notification.NotificationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserResponseServiceImpl implements UserResponseService {

    private UserRepository userRepository;
    private UserResponseRepository userResponseRepository;
    private MatchRepository matchRepository;
    private NotificationSender notificationSender;
    boolean fakeMatches;

    public UserResponseServiceImpl(UserRepository userRepository,
                                   UserResponseRepository userResponseRepository,
                                   MatchRepository matchRepository,
                                   NotificationSender notificationSender,
                                   @Value("${fakeMatches}") boolean fakeMatches) {
        this.userRepository = userRepository;
        this.userResponseRepository = userResponseRepository;
        this.matchRepository = matchRepository;
        this.notificationSender = notificationSender;
        this.fakeMatches = fakeMatches;
    }

    @Override
    @Transactional
    public void respond(Long userId, Long toUserId, Response response) {
        Assert.notNull(userId);
        Assert.notNull(toUserId);
        Assert.notNull(response);

        Optional<UserResponse> existingResponse = userResponseRepository.findByParties(userId, toUserId);
        if (existingResponse.isPresent()) {
            throw new IllegalStateException("Response from user " + userId + " to user " + toUserId + " already exists");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User " + userId + " not found"));
        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new IllegalArgumentException("User " + toUserId + " not found"));

        UserResponse userResponse = new UserResponse();
        userResponse.setUser(user);
        userResponse.setToUser(toUser);
        userResponse.setDate(new Date());
        userResponse.setResponse(response);
        userResponseRepository.save(userResponse);

        if (fakeMatches) {
            String description = toUser.getDescription();
            if (!StringUtils.isEmpty(description) && description.startsWith("Lorem ipsum") && description.endsWith("FAKE")) {
                UserResponse fakeResponse = new UserResponse();
                fakeResponse.setUser(toUser);
                fakeResponse.setToUser(user);
                fakeResponse.setDate(new Date());
                fakeResponse.setResponse(new Random().nextInt(2) == 1 ? Response.YES : Response.NO);
                userResponseRepository.save(fakeResponse);
            }
        }

        Optional<UserResponse> opponentResponseOpt = userResponseRepository.findByParties(toUserId, userId);
        if (opponentResponseOpt.isPresent()) {
            UserResponse opponentResponse = opponentResponseOpt.get();
            if (opponentResponse.getResponse() == Response.YES && response == Response.YES) {
                Match match = new Match();
                match.setMatchParties(new MatchParty(match, user), new MatchParty(match, toUser));
                match.setDate(new Date());
                matchRepository.save(match);
                sendMutualMatchNotifications(match);
            }
        }

    }

    private void sendMutualMatchNotifications(Match match) {
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

    private Map createPayload(Long id, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("matchId", id);
        map.put("opponentName", name);
        return map;
    }

}
