package lt.dualpair.server.service.user;

import lt.dualpair.core.user.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.Validate;

@Component
@Profile("!it")
public class SocialDataProviderFactory {

    private UsersConnectionRepository usersConnectionRepository;

    public SocialDataProvider getProvider(UserAccount.Type accountType, Long userId) {
        Validate.notNull(accountType, "Account type required");
        Validate.notNull(userId, "User id required");

        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(userId.toString());
        if (accountType == UserAccount.Type.FACEBOOK) {
            return new FacebookDataProvider(connectionRepository.findPrimaryConnection(Facebook.class));
        } else if (accountType == UserAccount.Type.VKONTAKTE) {
            return new VKontakteDataProvider(connectionRepository.findPrimaryConnection(VKontakte.class));
        } else {
            throw new IllegalArgumentException("Provider not found");
        }
    }

    @Autowired
    public void setUsersConnectionRepository(UsersConnectionRepository usersConnectionRepository) {
        this.usersConnectionRepository = usersConnectionRepository;
    }
}
