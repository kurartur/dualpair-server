package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserAccount;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.security.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity connect(@ActiveUser UserDetails principal,
                                  @RequestParam("provider") String authProviderId,
                                  @RequestParam("accessToken") String accessToken,
                                  @RequestParam(value = "expiresIn", required = false) Long expiresIn,
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

        usersConnectionRepository.createConnectionRepository(principal.getId().toString()).addConnection(connection);

        User freshUser = userRepository.findById(principal.getId()).orElseThrow(() -> new RuntimeException("User not found somehow"));
        UserAccount userAccount = new UserAccount(freshUser);
        userAccount.setAccountType(UserAccount.Type.valueOf(authProviderId.toUpperCase()));
        userAccount.setAccountId(connection.createData().getProviderUserId());
        freshUser.addUserAccount(userAccount);
        userRepository.save(freshUser);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/connect/{providerId:[a-zA-Z]+}")
    public ResponseEntity disconnect(@ActiveUser UserDetails principal,
                                     @PathVariable("providerId") String authProviderId) {
        User user = userRepository.findById(principal.getId()).orElseThrow(() -> new RuntimeException("User not found somehow"));
        if (user.getUserAccounts().size() < 2) {
            throw new IllegalArgumentException("Can't disconnect from last account");
        }
        UserAccount.Type accountType = UserAccount.Type.valueOf(authProviderId.toUpperCase());
        usersConnectionRepository.createConnectionRepository(principal.getId().toString()).removeConnections(accountType.name().toLowerCase());
        user.removeAccount(accountType);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

}
