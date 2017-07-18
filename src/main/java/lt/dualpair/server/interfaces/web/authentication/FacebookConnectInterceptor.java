package lt.dualpair.server.interfaces.web.authentication;

import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserAccount;
import lt.dualpair.core.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;

@Component
public class FacebookConnectInterceptor implements ConnectInterceptor<Facebook> {

    private UserRepository userRepository;

    @Inject
    public FacebookConnectInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void preConnect(ConnectionFactory<Facebook> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {}

    @Override
    public void postConnect(Connection<Facebook> connection, WebRequest request) {
        User user = fetchCurrentUser();
        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(UserAccount.Type.FACEBOOK);
        userAccount.setAccountId(connection.getApi().userOperations().getUserProfile().getId() + "");
        user.addUserAccount(userAccount);
        userRepository.save(user);
    }

    private User fetchCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(user.getId()).get(); // fetch fresh user from db
    }
}
