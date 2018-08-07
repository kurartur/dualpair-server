package lt.dualpair.server.security;

import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService, SocialUserDetailsService {

    private UserRepository userRepository;

    @Inject
    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(new Long(username));
        return new UserDetailsImpl(user.orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found.")).getId());
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(new Long(userId));
        return new UserDetailsImpl(user.orElseThrow(() -> new UsernameNotFoundException("User with user id " + userId + " not found.")).getId());

    }
}
