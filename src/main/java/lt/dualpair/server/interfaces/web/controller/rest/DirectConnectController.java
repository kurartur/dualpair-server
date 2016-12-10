package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class DirectConnectController {

    private SocialAuthenticationServiceLocator authServiceLocator;

    private UsersConnectionRepository usersConnectionRepository;

    private UserRepository userRepository;

    public DirectConnectController(SocialAuthenticationServiceLocator authServiceLocator, UsersConnectionRepository usersConnectionRepository,
                                   UserRepository userRepository) {
        this.authServiceLocator = authServiceLocator;
        this.usersConnectionRepository = usersConnectionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/connect")
    @Transactional
    public ResponseEntity connect(@ActiveUser User user,
                                  @RequestParam("provider") String authProviderId,
                                  @RequestParam("accessToken") String accessToken,
                                  @RequestParam("expiresIn") Long expiresIn,
                                  @RequestParam(value = "scope", required = false) String scope) {

        AccessGrant accessGrant = new AccessGrant(accessToken, scope, null, expiresIn);

        Set<String> authProviders = authServiceLocator.registeredAuthenticationProviderIds();
        if (authProviders.isEmpty() || authProviderId == null || !authProviders.contains(authProviderId)) {
            throw new InvalidGrantException("Unrecognized social provider");
        }

        SocialAuthenticationService<?> authService = authServiceLocator.getAuthenticationService(authProviderId);
        ConnectionFactory<?> connectionFactory = authService.getConnectionFactory();
        if (!(connectionFactory instanceof OAuth2ConnectionFactory)) {
            throw new InvalidGrantException("Only OAuth2ConnectionFactory supported");
        }
        OAuth2ConnectionFactory oAuth2ConnectionFactory = (OAuth2ConnectionFactory)connectionFactory;
        Connection<?> connection = oAuth2ConnectionFactory.createConnection(accessGrant);

        // TODO validate connection

        usersConnectionRepository.createConnectionRepository(user.getUsername()).addConnection(connection);

        User freshUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found somehow"));
        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(UserAccount.Type.valueOf(authProviderId.toUpperCase()));
        userAccount.setAccountId(connection.createData().getProviderUserId());
        freshUser.addUserAccount(userAccount);
        userRepository.save(freshUser);

        return ResponseEntity.ok().build();
    }

}
