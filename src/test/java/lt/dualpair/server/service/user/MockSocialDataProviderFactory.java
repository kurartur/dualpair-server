package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.user.UserAccount;
import org.springframework.context.annotation.Profile;
import org.springframework.social.connect.Connection;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("it")
public class MockSocialDataProviderFactory extends SocialDataProviderFactory {

    private static Map<UserAccount.Type, SocialDataProvider> socialDataProviders = new HashMap<>();

    @Override
    public SocialDataProvider getProvider(Connection connection) {
        return new MockSocialDataProvider();
    }

    @Override
    public SocialDataProvider getProvider(UserAccount.Type accountType, String username) {
        if (socialDataProviders.containsKey(accountType)) {
            return socialDataProviders.get(accountType);
        }
        return new MockSocialDataProvider();
    }

    public static void setSocialDataProvider(UserAccount.Type accountType, SocialDataProvider socialDataProvider) {
        socialDataProviders.put(accountType, socialDataProvider);
    }
}
