package lt.dualpair.server.service.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.user.MatchRepository;
import lt.dualpair.core.user.Response;
import lt.dualpair.core.user.UserResponse;
import lt.dualpair.core.user.UserResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Date;

@Service
public class UserMatchServiceImpl implements UserMatchService {

    private MatchRepository matchRepository;
    private UserResponseRepository userResponseRepository;

    @Inject
    public UserMatchServiceImpl(MatchRepository matchRepository, UserResponseRepository userResponseRepository) {
        this.matchRepository = matchRepository;
        this.userResponseRepository = userResponseRepository;
    }

    @Override
    @Transactional
    public void remove(Long matchId, Long userId) {
        Assert.notNull(matchId);
        Assert.notNull(userId);

        Match match = matchRepository.findOneByUser(userId, matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match with id " + matchId + " and user id " + userId + " not found"));
        Long opponentUserId = match.getOppositeMatchParty(userId).getUser().getId();
        UserResponse userResponse = userResponseRepository.findByParties(userId, opponentUserId)
                .get();
        UserResponse opponentResponse = userResponseRepository.findByParties(opponentUserId, userId)
                .get();

        userResponseRepository.delete(userResponse);
        userResponseRepository.delete(opponentResponse);
        matchRepository.delete(match);
    }

    @Override
    @Transactional
    public void unmatch(Long matchId, Long userId) {
        Assert.notNull(matchId);
        Assert.notNull(userId);

        Match match = matchRepository.findOneByUser(userId, matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match with id " + matchId + " and user id " + userId + " not found"));
        Long opponentUserId = match.getOppositeMatchParty(userId).getUser().getId();
        UserResponse userResponse = userResponseRepository.findByParties(userId, opponentUserId)
                .get();
        UserResponse opponentResponse = userResponseRepository.findByParties(opponentUserId, userId)
                .get();

        userResponse.setMatch(null);
        userResponse.setDate(new Date());
        userResponse.setResponse(Response.NO);

        opponentResponse.setMatch(null);

        userResponseRepository.save(userResponse);
        userResponseRepository.save(opponentResponse);
        matchRepository.delete(matchId);
    }
}
