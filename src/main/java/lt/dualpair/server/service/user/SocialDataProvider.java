package lt.dualpair.server.service.user;

import lt.dualpair.core.user.User;

public interface SocialDataProvider {

    String getAccountId();

    User enhanceUser(User user) throws SocialDataException;

}
