package lt.dualpair.server.interfaces.resource.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class MatchResource extends ResourceSupport {

    @JsonProperty("id")
    private Long matchId;

    private UserMatchPartyResource user;

    private OpponentMatchPartyResource opponent;

    private Integer distance;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public UserMatchPartyResource getUser() {
        return user;
    }

    public void setUser(UserMatchPartyResource user) {
        this.user = user;
    }

    public OpponentMatchPartyResource getOpponent() {
        return opponent;
    }

    public void setOpponent(OpponentMatchPartyResource opponent) {
        this.opponent = opponent;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}
