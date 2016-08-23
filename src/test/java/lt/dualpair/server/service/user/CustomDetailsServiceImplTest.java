package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CustomDetailsServiceImplTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private CustomDetailsServiceImpl customDetailsService = new CustomDetailsServiceImpl();

    @Before
    public void setUp() throws Exception {
        customDetailsService.setUserRepository(userRepository);
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        User user = UserTestUtils.createUser(1L, "username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        User resultUser = customDetailsService.loadUserByUsername("username");
        assertEquals(user, resultUser);
    }

    @Test
    public void testLoadUserByUsername_notFound() throws Exception {
        try {
            when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
            customDetailsService.loadUserByUsername("username");
            fail();
        } catch (UsernameNotFoundException unfe) {
            assertTrue(unfe.getMessage().equals("User with username username not found."));
        }
    }

    @Test
    public void testLoadUserByUserId() throws Exception {
        User user = UserTestUtils.createUser(1L, "username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        User resultUser = customDetailsService.loadUserByUserId("username");
        verify(userRepository, times(1)).findByUsername("username");
        assertEquals(user, resultUser);
    }

    @Test
    public void testLoadUserByUserId_notFound() throws Exception {
        try {
            when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
            customDetailsService.loadUserByUserId("username");
            fail();
        } catch (UsernameNotFoundException unfe) {
            assertTrue(unfe.getMessage().equals("User with username username not found."));
        }
    }

}