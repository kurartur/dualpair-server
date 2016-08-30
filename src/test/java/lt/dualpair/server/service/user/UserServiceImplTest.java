package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserServiceImpl userService = new UserServiceImpl();
    private UserRepository userRepository = mock(UserRepository.class);
    private SociotypeRepository sociotypeRepository = mock(SociotypeRepository.class);

    @Before
    public void setUp() throws Exception {
        userService.setUserRepository(userRepository);
        userService.setSociotypeRepository(sociotypeRepository);
    }

    @Test
    public void testLoadUserById() throws Exception {
        User user = UserTestUtils.createUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User resultUser = userService.loadUserById(1L);
        assertEquals(user, resultUser);
    }

    @Test
    public void testLoadUserById_notFound() throws Exception {
        try {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            userService.loadUserById(1L);
            fail();
        } catch (UserNotFoundException unfe) {
            assertTrue(unfe.getMessage().equals("User with ID 1 not found."));
        }
    }

    @Test
    public void testBuildAlternativeId() throws Exception {
        String hash = userService.buildUserId("mymail@google.com", 123456789L);
        assertEquals("fb77d8e5c02d0dac8d7d12bcdff36f5a", hash);
    }

    @Test
    public void testSetUserSociotypes_nullParameters() throws Exception {
        try {
            userService.setUserSociotypes(null, null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("User id is mandatory", iae.getMessage());
        }
        
        try {
            userService.setUserSociotypes(1L, null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Sociotype codes are mandatory", iae.getMessage());
        }
    }

    @Test
    public void testSetUserSociotypes_sociotypesNotFound() throws Exception {
        Set<Sociotype.Code1> codes = new HashSet<>();
        codes.add(Sociotype.Code1.EII);
        List<Sociotype.Code1> codeList = new ArrayList<>(codes);
        User user = UserTestUtils.createUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sociotypeRepository.findByCode1List(codeList)).thenReturn(new HashSet<>());
        try {
            userService.setUserSociotypes(1L, codes);
            fail();
        } catch (IllegalStateException ise) {
            assertEquals("Zero sociotypes found", ise.getMessage());
        }
    }

    @Test
    public void testSetUserSociotypes() throws Exception {
        Set<Sociotype> sociotypes = new HashSet<>();
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.EII).build());
        Set<Sociotype.Code1> codes = new HashSet<>();
        codes.add(Sociotype.Code1.EII);
        ArrayList<Sociotype.Code1> codeList = new ArrayList<>(codes);
        User user = UserTestUtils.createUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sociotypeRepository.findByCode1List(codeList)).thenReturn(sociotypes);
        userService.setUserSociotypes(1L, codes);
        assertEquals(sociotypes, user.getSociotypes());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSetUserSociotypes_invalidCount() throws Exception {
        Set<Sociotype.Code1> codes = new HashSet<>();

        try {
            userService.setUserSociotypes(1L, codes);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid sociotype code count. Must be 1 or 2", iae.getMessage());
        }
        verify(userRepository, never()).save(any(User.class));

        codes.add(Sociotype.Code1.EII);
        codes.add(Sociotype.Code1.LSE);
        codes.add(Sociotype.Code1.IEI);

        try {
            userService.setUserSociotypes(1L, codes);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid sociotype code count. Must be 1 or 2", iae.getMessage());
        }
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testSetUserDateOfBirth() throws Exception {
        User user = new User();
        Date date = new Date();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.setUserDateOfBirth(1L, date);
        verify(userRepository, times(1)).save(user);
        assertEquals(date, user.getDateOfBirth());
    }

    @Test
    public void testSetUserSearchParameters() throws Exception {
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchFemale(true);
        searchParameters.setSearchMale(true);
        searchParameters.setMinAge(20);
        searchParameters.setMaxAge(30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.setUserSearchParameters(1L, searchParameters);
        assertNotEquals(searchParameters, user.getSearchParameters());
        SearchParameters resultsSearchParameters = user.getSearchParameters();
        assertTrue(resultsSearchParameters.getSearchFemale());
        assertTrue(resultsSearchParameters.getSearchMale());
        assertEquals((Integer)20, resultsSearchParameters.getMinAge());
        assertEquals((Integer)30, resultsSearchParameters.getMaxAge());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSetUserSearchParameters_noParameters() throws Exception {
        User user = new User();
        user.setSearchParameters(null);
        SearchParameters searchParameters = new SearchParameters();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.setUserSearchParameters(1L, searchParameters);
        assertEquals(searchParameters, user.getSearchParameters());
        assertEquals(user, searchParameters.getUser());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User();
        userService.updateUser(user);
        verify(userRepository, times(1)).save(user);
        assertNotNull(user.getDateUpdated());
    }
}