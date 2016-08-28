package lt.dualpair.server.interfaces.resource.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class MatchResource extends ResourceSupport {

    @JsonProperty("id")
    private Long matchId;

    private BasicMatchPartyResource user;

    private FullMatchPartyResource opponent;

    private Integer distance;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public BasicMatchPartyResource getUser() {
        return user;
    }

    public void setUser(BasicMatchPartyResource user) {
        this.user = user;
    }

    public FullMatchPartyResource getOpponent() {
        return opponent;
    }

    public void setOpponent(FullMatchPartyResource opponent) {
        this.opponent = opponent;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}
