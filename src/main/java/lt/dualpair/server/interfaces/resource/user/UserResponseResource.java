package lt.dualpair.server.interfaces.resource.user;

import org.springframework.hateoas.ResourceSupport;

public class UserResponseResource extends ResourceSupport {

    private UserResource user;
    private String response;

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
}
