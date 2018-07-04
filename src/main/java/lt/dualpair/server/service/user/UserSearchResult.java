package lt.dualpair.server.service.user;

import lt.dualpair.core.user.User;

public class UserSearchResult {

    private User user;
    private Integer distance;

    public UserSearchResult(User user, Integer distance) {
        this.user = user;
        this.distance = distance;
    }

    public User getUser() {
        return user;
    }

    public Integer getDistance() {
        return distance;
    }
}
