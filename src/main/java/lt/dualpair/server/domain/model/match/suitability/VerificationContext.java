package lt.dualpair.server.domain.model.match.suitability;

import lt.dualpair.server.domain.model.user.User;

public class VerificationContext {

    private User user;

    public VerificationContext(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
