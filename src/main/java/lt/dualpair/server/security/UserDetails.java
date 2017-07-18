package lt.dualpair.server.security;

import org.springframework.social.security.SocialUserDetails;

public interface UserDetails extends SocialUserDetails {

    Long getId();

}
