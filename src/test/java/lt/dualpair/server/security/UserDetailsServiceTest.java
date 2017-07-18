package lt.dualpair.server.security;

import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.core.user.UserTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserDetailsServiceTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private UserDetailsService userDetailsService;

    @Before
    public void setUp() throws Exception {
        userDetailsService = new UserDetailsService(userRepository);
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        User user = UserTestUtils.createUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername("1");
        assertEquals("1", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_notFound() throws Exception {
        try {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            userDetailsService.loadUserByUsername("1");
            fail();
        } catch (UsernameNotFoundException unfe) {
            assertTrue(unfe.getMessage().equals("User with username 1 not found."));
        }
    }

    @Test
    public void testLoadUserByUserId() throws Exception {
        User user = UserTestUtils.createUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        SocialUserDetails socialUserDetails = userDetailsService.loadUserByUserId("1");
        verify(userRepository, times(1)).findById(1L);
        assertEquals("1", socialUserDetails.getUserId());
    }

    @Test
    public void testLoadUserByUserId_notFound() throws Exception {
        try {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            userDetailsService.loadUserByUserId("1");
            fail();
        } catch (UsernameNotFoundException unfe) {
            assertTrue(unfe.getMessage().equals("User with user id 1 not found."));
        }
    }

}