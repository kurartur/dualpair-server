package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.user.User;

public interface SocialDataProvider {

    String getAccountId();

    User enhanceUser(User user) throws SocialDataException;

}
