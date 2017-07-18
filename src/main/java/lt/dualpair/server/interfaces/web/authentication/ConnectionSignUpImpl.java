package lt.dualpair.server.interfaces.web.authentication;

import lt.dualpair.core.match.SearchParameters;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserAccount;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.server.service.user.FacebookDataProvider;
import lt.dualpair.server.service.user.SocialDataException;
import lt.dualpair.server.service.user.SocialDataProvider;
import lt.dualpair.server.service.user.VKontakteDataProvider;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.Validate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class ConnectionSignUpImpl implements ConnectionSignUp {

    private static final Logger logger = Logger.getLogger(ConnectionSignUpImpl.class.getName());

    private static final Integer DEFAULT_SEARCH_AGE_GAP = 3;

    private UserRepository userRepository;

    @Override
    public String execute(Connection<?> connection) {
        try {
            Validate.notNull(connection, "Connection required");

            SocialDataProvider socialDataProvider = getProvider(connection);

            String accountId = socialDataProvider.getAccountId();

            UserAccount.Type accountType;
            if (socialDataProvider instanceof FacebookDataProvider) {
                accountType = UserAccount.Type.FACEBOOK;
            } else if (socialDataProvider instanceof VKontakteDataProvider) {
                accountType = UserAccount.Type.VKONTAKTE;
            } else {
                throw new IllegalArgumentException("Unsupported connection type");
            }

            User newUser = buildUser(accountId, accountType);
            newUser = socialDataProvider.enhanceUser(newUser);
            setDefaultSearchParameters(newUser);
            userRepository.save(newUser);
            return newUser.getId().toString();
        } catch (SocialDataException sce) {
            throw new RuntimeException(sce);
        }
    }

    private void setDefaultSearchParameters(User user) {
        SearchParameters searchParameters = user.getSearchParameters();
        if (user.getAge() != null) {
            searchParameters.setMinAge(user.getAge() - DEFAULT_SEARCH_AGE_GAP);
            searchParameters.setMaxAge(user.getAge() + DEFAULT_SEARCH_AGE_GAP);
        }
        if (user.getGender() == Gender.MALE) {
            searchParameters.setSearchFemale(true);
            searchParameters.setSearchMale(false);
        } else {
            searchParameters.setSearchMale(true);
            searchParameters.setSearchFemale(false);
        }
        searchParameters.setUser(user);
        user.setSearchParameters(searchParameters);
    }

    private User buildUser(String accountId, UserAccount.Type accountType) {
        User user = new User();
        Long currentTime = System.currentTimeMillis();
        Date currentDate = new Date(currentTime);
        user.setDateCreated(currentDate);
        user.setDateUpdated(currentDate);

        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(accountType);
        userAccount.setAccountId(accountId);
        Set<UserAccount> userAccounts = new HashSet<>();
        userAccounts.add(userAccount);
        user.setUserAccounts(userAccounts);

        return user;
    }

    protected SocialDataProvider getProvider(Connection connection) {
        Validate.notNull(connection, "Connection required");

        if (connection.getApi() instanceof Facebook) {
            return new FacebookDataProvider(connection);
        } else if (connection.getApi() instanceof VKontakte) {
            return new VKontakteDataProvider(connection);
        } else {
            throw new IllegalArgumentException("Provider not found");
        }
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
