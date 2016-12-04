package lt.dualpair.server.infrastructure.authentication;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            return newUser.getUsername();
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
        if (user.getGender() == User.Gender.MALE) {
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
        user.setUsername(buildUserId(accountId, currentTime));

        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(accountType);
        userAccount.setAccountId(accountId);
        Set<UserAccount> userAccounts = new HashSet<>();
        userAccounts.add(userAccount);
        user.setUserAccounts(userAccounts);

        return user;
    }

    protected String buildUserId(String accountId, Long time) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(accountId.getBytes("UTF-8"));
            outputStream.write(time.toString().getBytes("UTF-8"));

            byte[] md5 = messageDigest.digest(outputStream.toByteArray());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < md5.length; ++i)
                sb.append(Integer.toHexString((md5[i] & 0xFF) | 0x100).substring(1,3));

            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("Was not able to generate user id: " + e.getMessage(), e);
            return time.toString();
        }
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
