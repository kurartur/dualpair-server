package lt.dualpair.server.service.user;

import lt.dualpair.core.user.UserAccount;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("it")
public class MockSocialDataProviderFactory extends SocialDataProviderFactory {

    private static Map<UserAccount.Type, SocialDataProvider> socialDataProviders = new HashMap<>();

    @Override
    public SocialDataProvider getProvider(UserAccount.Type accountType, Long userId) {
        if (socialDataProviders.containsKey(accountType)) {
            return socialDataProviders.get(accountType);
        }
        return new MockSocialDataProvider();
    }

    public static void setSocialDataProvider(UserAccount.Type accountType, SocialDataProvider socialDataProvider) {
        socialDataProviders.put(accountType, socialDataProvider);
    }
}
