package lt.dualpair.server.infrastructure.authentication;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;

@Component
public class VKontakteConnectInterceptor implements ConnectInterceptor<VKontakte> {

    private UserRepository userRepository;

    @Inject
    public VKontakteConnectInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void preConnect(ConnectionFactory<VKontakte> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {}

    @Override
    public void postConnect(Connection<VKontakte> connection, WebRequest request) {
        User user = fetchCurrentUser();
        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(UserAccount.Type.VKONTAKTE);
        userAccount.setAccountId(connection.getApi().usersOperations().getUser().getId() + "");
        user.addUserAccount(userAccount);
        userRepository.save(user);
    }

    private User fetchCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(user.getId()).get(); // fetch fresh user from db
    }
}
