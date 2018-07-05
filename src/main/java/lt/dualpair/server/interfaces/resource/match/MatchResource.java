package lt.dualpair.server.interfaces.resource.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import org.springframework.hateoas.ResourceSupport;

import java.util.Date;

public class MatchResource extends ResourceSupport {

    @JsonProperty("id")
    private Long matchId;
    private UserResource user;
    private Date date;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
