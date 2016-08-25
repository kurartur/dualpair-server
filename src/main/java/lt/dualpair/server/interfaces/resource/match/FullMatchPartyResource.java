package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.interfaces.resource.user.UserResource;

public class FullMatchPartyResource extends BasicMatchPartyResource {

    private UserResource user;

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }
}
