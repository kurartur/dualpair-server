package lt.dualpair.server;

import lt.dualpair.core.user.User;
import lt.dualpair.server.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
@Profile("it")
public class OAuthHelper {

    // For use with MockMvc
    public RequestPostProcessor bearerToken(final String clientid, final UserDetailsImpl userPrincipal) {
        return mockRequest -> {
            OAuth2AccessToken token = createAccessToken(clientid, userPrincipal);
            mockRequest.addHeader("Authorization", "Bearer " + token.getValue());
            return mockRequest;
        };
    }

    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired
    @Qualifier("defaultTokenServices")
    AuthorizationServerTokenServices tokenservice;

    OAuth2AccessToken createAccessToken(final String clientId, final UserDetailsImpl userPrincipal) {
        // Look up authorities, resourceIds and scopes based on clientId
        ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
        Collection<GrantedAuthority> authorities = client.getAuthorities();
        Set<String> resourceIds = client.getResourceIds();
        Set<String> scopes = client.getScope();

        // Default values for other parameters
        Map<String, String> requestParameters = Collections.emptyMap();
        boolean approved = true;
        String redirectUrl = null;
        Set<String> responseTypes = Collections.emptySet();
        Map<String, Serializable> extensionProperties = Collections.emptyMap();

        // Create request
        OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId, authorities, approved, scopes,
                resourceIds, redirectUrl, responseTypes, extensionProperties);

        // Create OAuth2AccessToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
        OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);
        return tokenservice.createAccessToken(auth);
    }

    public static User buildUserPrincipal(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

}
