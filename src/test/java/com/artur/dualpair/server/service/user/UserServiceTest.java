package com.artur.dualpair.server.service.user;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.persistence.repository.SociotypeRepository;
import com.artur.dualpair.server.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService = new UserService();
    private UserRepository userRepository = mock(UserRepository.class);
    private SociotypeRepository sociotypeRepository = mock(SociotypeRepository.class);

    @Before
    public void setUp() throws Exception {
        userService.setUserRepository(userRepository);
        userService.setSociotypeRepository(sociotypeRepository);
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        User user = createUser(1L, "username", "email");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        User resultUser = userService.loadUserByUsername("username");
        verify(userRepository, times(1)).findByUsername("username");
        assertEquals(user, resultUser);
    }

    @Test
    public void testLoadUserByUsername_notFound() throws Exception {
        try {
            when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
            userService.loadUserByUsername("username");
            fail();
        } catch (UsernameNotFoundException unfe) {
            assertTrue(unfe.getMessage().equals("User with username username not found."));
        }
    }

    @Test
    public void testLoadUserByUserId() throws Exception {
        User user = createUser(1L, "username", "email");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        User resultUser = userService.loadUserByUserId("username");
        verify(userRepository, times(1)).findByUsername("username");
        assertEquals(user, resultUser);
    }

    @Test
    public void testLoadUserByUserId_notFound() throws Exception {
        try {
            when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
            userService.loadUserByUserId("username");
            fail();
        } catch (UsernameNotFoundException unfe) {
            assertTrue(unfe.getMessage().equals("User with username username not found."));
        }
    }

    @Test
    public void testBuildAlternativeId() throws Exception {
        String hash = userService.buildUserId("mymail@google.com", 123456789L);
        assertEquals("fb77d8e5c02d0dac8d7d12bcdff36f5a", hash);
    }

    @Test
    public void testUpdateUserSociotypes() throws Exception {
        Set<Sociotype> sociotypes = new HashSet<>();
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.EII).build());
        Set<Sociotype.Code1> codes = new HashSet<>();
        codes.add(Sociotype.Code1.EII);
        ArrayList<Sociotype.Code1> codeList = new ArrayList<>(codes);
        User user = createUser(1L, "username", null);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(sociotypeRepository.findByCode1List(codeList)).thenReturn(sociotypes);
        userService.setUserSociotypes("username", codes);
        assertEquals(sociotypes, user.getSociotypes());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUserSociotypes_invalidCount() throws Exception {
        Set<Sociotype> sociotypes = new HashSet<>();
        Set<Sociotype.Code1> codes = new HashSet<>();
        ArrayList<Sociotype.Code1> codeList = new ArrayList<>(codes);
        User user = createUser(1L, "username", null);
        Set<Sociotype> userSociotypes = new HashSet<>();
        userSociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.LSE).build());
        user.setSociotypes(userSociotypes);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(sociotypeRepository.findByCode1List(codeList)).thenReturn(sociotypes);

        try {
            userService.setUserSociotypes("username", codes);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("User must have 1 or 2 sociotypes", iae.getMessage());
        }
        assertEquals(userSociotypes, user.getSociotypes());
        verify(userRepository, never()).save(user);

        codes.add(Sociotype.Code1.EII);
        codes.add(Sociotype.Code1.LSE);
        codes.add(Sociotype.Code1.IEI);
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.EII).build());
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.LSE).build());
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.IEI).build());

        try {
            userService.setUserSociotypes("username", codes);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("User must have 1 or 2 sociotypes", iae.getMessage());
        }
        assertEquals(userSociotypes, user.getSociotypes());
        verify(userRepository, never()).save(user);

    }

    private User createUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}