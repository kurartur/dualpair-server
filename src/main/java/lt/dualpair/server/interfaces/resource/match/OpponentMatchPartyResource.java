package lt.dualpair.server.interfaces.resource.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import org.springframework.hateoas.ResourceSupport;

public class OpponentMatchPartyResource extends ResourceSupport {

    @JsonProperty("id")
    private Long partyId;
    private UserResource user;

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }
}
