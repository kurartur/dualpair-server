package lt.dualpair.server.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class Authorizer {

    public boolean hasPermission(Authentication authentication, Long userId) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getId().equals(userId);
    }

}
