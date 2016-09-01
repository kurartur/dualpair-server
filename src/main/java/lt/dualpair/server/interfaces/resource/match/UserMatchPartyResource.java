package lt.dualpair.server.interfaces.resource.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class UserMatchPartyResource extends ResourceSupport {

    @JsonProperty("id")
    private Long partyId;
    private String response;

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
