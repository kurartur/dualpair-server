package lt.dualpair.server.security;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SocialTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "social";

    private final AuthenticationManager authenticationManager;
    private final SocialAuthenticationServiceLocator authServiceLocator;

    public SocialTokenGranter(AuthenticationManager authenticationManager, SocialAuthenticationServiceLocator authServiceLocator,
                                             AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(authenticationManager, authServiceLocator, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    protected SocialTokenGranter(AuthenticationManager authenticationManager, SocialAuthenticationServiceLocator authServiceLocator,
                                             AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
        this.authServiceLocator = authServiceLocator;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());

        String accessToken = parameters.get("access_token");
        String scope = parameters.get("scope");
        Long expiresIn = StringUtils.isEmpty(parameters.get("expires_in")) ? null : Long.valueOf(parameters.get("expires_in"));
        String authProviderId = parameters.get("provider");

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
        Authentication userAuth = new SocialAuthenticationToken(connection, null);

        try {
            userAuth = authenticationManager.authenticate(userAuth);
        }
        catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }
        catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            throw new InvalidGrantException(e.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user");
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }

}
