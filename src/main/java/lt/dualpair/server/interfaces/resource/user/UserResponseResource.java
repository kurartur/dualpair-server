package lt.dualpair.server.interfaces.resource.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.hateoas.ResourceSupport;

import java.util.Date;

public class UserResponseResource extends ResourceSupport {

    private UserResource user;
    private String response;
    private boolean match;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
