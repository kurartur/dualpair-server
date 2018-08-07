package lt.dualpair.server.service.user;

import lt.dualpair.core.user.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("it")
public class MockSocialDataProvider implements SocialDataProvider {

    @Override
    public String getAccountId() {
        return null;
    }

    @Override
    public User enhanceUser(User user) throws SocialDataException {
        return null;
    }

}
