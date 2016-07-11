package com.artur.dualpair.server.service.user;

import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.domain.model.user.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.Validate;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service("socialUserService")
public class SocialUserService extends UserService implements SocialUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SocialUserService.class.getName());

    private SocialDataProviderFactory socialDataProviderFactory;

    public User getUser(String userId) {
        Validate.notNull(userId, "User id required");
        User user = loadUserByUserId(userId);
        return user;
    }

    public User loadOrCreate(Connection connection) throws SocialDataException {
        Validate.notNull(connection, "Connection required");

        SocialDataProvider socialDataProvider = socialDataProviderFactory.getProvider(connection);

        String accountId = socialDataProvider.getAccountId();

        UserAccount.Type accountType;
        if (socialDataProvider instanceof FacebookDataProvider) {
            accountType = UserAccount.Type.FACEBOOK;
        } else {
            throw new IllegalArgumentException("Unsupported connection type");
        }

        Optional<User> user = userRepository.findByAccountId(accountId, accountType);
        if (user.isPresent()) {
            return user.get();
        } else {
            User newUser = buildUser(accountId, accountType);
            newUser = socialDataProvider.enhanceUser(newUser);
            userRepository.save(newUser);
            return newUser;
        }
    }

    private User buildUser(String accountId, UserAccount.Type accountType) {
        User user = new User();
        Long currentTime = System.currentTimeMillis();
        Date currentDate = new Date(currentTime);
        user.setCreated(currentDate);
        user.setUpdated(currentDate);
        user.setUsername(buildUserId(accountId, currentTime));

        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(accountType);
        userAccount.setAccountId(accountId);
        Set<UserAccount> userAccounts = new HashSet<>();
        userAccounts.add(userAccount);
        user.setUserAccounts(userAccounts);

        return user;
    }

    @Autowired
    public void setSocialDataProviderFactory(SocialDataProviderFactory socialDataProviderFactory) {
        this.socialDataProviderFactory = socialDataProviderFactory;
    }
}
