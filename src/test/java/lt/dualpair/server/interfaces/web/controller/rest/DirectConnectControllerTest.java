package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserAccount;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.server.security.UserDetailsImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class DirectConnectControllerTest {

    private DirectConnectController controller;
    private UsersConnectionRepository usersConnectionRepository = mock(UsersConnectionRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private ConnectionRepository connectionRepository = mock(ConnectionRepository.class);

    @Before
    public void setUp() throws Exception {
        controller = new DirectConnectController(null, usersConnectionRepository, userRepository);
        when(usersConnectionRepository.createConnectionRepository("1")).thenReturn(connectionRepository);
    }

    @Test
    public void disconnect_whenOnlyOneAccountExists_exceptionThrown() {
        User user = new User();
        Set<UserAccount> userAccountSet = new HashSet<>();
        userAccountSet.add(new UserAccount(user));
        user.setUserAccounts(userAccountSet);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        try {
            controller.disconnect(new UserDetailsImpl(1L), "provider");
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Can't disconnect from last account", iae.getMessage());
        }
    }

    @Test
    public void disconnect() {
        User user = new User();
        Set<UserAccount> userAccountSet = new HashSet<>();
        userAccountSet.add(new UserAccount(user));
        UserAccount fbAccount = new UserAccount(user);
        fbAccount.setAccountType(UserAccount.Type.FACEBOOK);
        userAccountSet.add(fbAccount);
        user.setUserAccounts(userAccountSet);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ResponseEntity result = controller.disconnect(new UserDetailsImpl(1L), "facebook");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(connectionRepository, times(1)).removeConnections("facebook");
        verify(userRepository, times(1)).save(user);
        assertEquals(1, user.getUserAccounts().size());
    }
}