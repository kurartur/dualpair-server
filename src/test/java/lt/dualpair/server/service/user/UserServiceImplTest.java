package lt.dualpair.server.service.user;

import lt.dualpair.core.match.*;
import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.photo.PhotoRepository;
import lt.dualpair.core.socionics.RelationType;
import lt.dualpair.core.socionics.Sociotype;
import lt.dualpair.core.socionics.SociotypeRepository;
import lt.dualpair.core.user.MatchRepository;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.core.user.UserTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserServiceImpl userService = new UserServiceImpl();
    private UserRepository userRepository = mock(UserRepository.class);
    private SociotypeRepository sociotypeRepository = mock(SociotypeRepository.class);
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private PhotoRepository photoRepository = mock(PhotoRepository.class);

    @Before
    public void setUp() throws Exception {
        userService.setUserRepository(userRepository);
        userService.setSociotypeRepository(sociotypeRepository);
        userService.setMatchRepository(matchRepository);
        userService.setPhotoRepository(photoRepository);
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
    public void testSetUserSociotypes_invalidCount() throws Exception {
        Set<Sociotype> sociotypes = new HashSet<>();
        User user = UserTestUtils.createUser();

        try {
            userService.setUserSociotypes(user, sociotypes);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid sociotype code count. Must be 1 or 2", iae.getMessage());
        }
        verify(userRepository, never()).save(any(User.class));
        verify(matchRepository, never()).delete(any(Match.class));

        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.IEE).build());
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.LSE).build());
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.IEI).build());

        try {
            userService.setUserSociotypes(user, sociotypes);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid sociotype code count. Must be 1 or 2", iae.getMessage());
        }
        verify(userRepository, never()).save(any(User.class));
        verify(matchRepository, never()).delete(any(Match.class));
    }

    @Test
    public void testSetUserSociotypes_invalidParameters() throws Exception {
        try {
            userService.setUserSociotypes(null, null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("User is mandatory", iae.getMessage());
        }

        try {
            userService.setUserSociotypes(UserTestUtils.createUser(), null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Sociotypes are mandatory", iae.getMessage());
        }

        try {
            Set<Sociotype> sociotypes = new HashSet<>();
            sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.IEE).build());
            userService.setUserSociotypes(null, sociotypes);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("User is mandatory", iae.getMessage());
        }

        try {
            userService.setUserSociotypes(UserTestUtils.createUser(), new HashSet<>());
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid sociotype code count. Must be 1 or 2", iae.getMessage());
        }
    }

    @Test
    public void testSetUserSociotypes_sameSociotype() throws Exception {
        User user = UserTestUtils.createUser();
        Set<Sociotype> newSociotypes = new HashSet<>(user.getSociotypes());
        Sociotype opposite = new Sociotype.Builder().code1(Sociotype.Code1.LSI).build();
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.IEE, RelationType.Code.DUAL)).thenReturn(opposite);
        userService.setUserSociotypes(user, newSociotypes);
        verify(matchRepository, never()).findForPossibleRemoval(any(User.class));
        assertEquals(Sociotype.Code1.IEE, user.getSociotypes().iterator().next().getCode1());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSetUserSociotypes_differentSociotype() throws Exception {
        User user = UserTestUtils.createUser();
        Sociotype oldSociotype = user.getSociotypes().iterator().next();
        Set<Sociotype> newSociotypes = new HashSet<>();
        newSociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.EII).build());
        Sociotype opposite = new Sociotype.Builder().code1(Sociotype.Code1.LSI).build();
        Match match = MatchTestUtils.createMatch();
        Set<Match> matches = new HashSet<>();
        matches.add(match);
        when(matchRepository.findForPossibleRemoval(user)).thenReturn(matches);
        when(sociotypeRepository.findOppositeByRelationType(oldSociotype.getCode1(), RelationType.Code.DUAL)).thenReturn(opposite);
        userService.setUserSociotypes(user, newSociotypes);
        verify(matchRepository, times(1)).findForPossibleRemoval(user);
        verify(matchRepository, times(1)).delete(match);
        verify(userRepository, times(1)).save(user);
    }

    /**
     *  Users were linked by one sociotype, that gets removed.
     *  Match should be removed.
     */
    @Test
    public void testSetUserSociotypes_invalidMatch() throws Exception {
        Sociotype sleSociotype = new Sociotype.Builder().code1(Sociotype.Code1.SLE).build();
        Sociotype sliSociotype = new Sociotype.Builder().code1(Sociotype.Code1.SLI).build();
        Sociotype iliSociotype = new Sociotype.Builder().code1(Sociotype.Code1.ILI).build();
        Sociotype eseSociotype = new Sociotype.Builder().code1(Sociotype.Code1.ESE).build();

        User opponentUser = UserTestUtils.createUser(2L);
        Set<Sociotype> opponentSociotypes = new HashSet<>();
        opponentSociotypes.add(sleSociotype);
        opponentSociotypes.add(sliSociotype);
        opponentUser.setSociotypes(opponentSociotypes);

        User user = UserTestUtils.createUser();
        Sociotype oldSociotype1 = new Sociotype.Builder().code1(Sociotype.Code1.ILE).build();
        Sociotype oldsociotype2 = new Sociotype.Builder().code1(Sociotype.Code1.LSE).build();
        Set<Sociotype> oldSociotypes = new HashSet<>();
        oldSociotypes.add(oldSociotype1);
        oldSociotypes.add(oldsociotype2);
        user.setSociotypes(oldSociotypes);

        Sociotype newSociotype1 = new Sociotype.Builder().code1(Sociotype.Code1.LSE).build();
        Sociotype newSociotype2 = new Sociotype.Builder().code1(Sociotype.Code1.SEE).build();
        Set<Sociotype> newSociotypes = new HashSet<>();
        newSociotypes.add(newSociotype1);
        newSociotypes.add(newSociotype2);

        Match match = MatchTestUtils.createMatch(1L,
                MatchPartyTestUtils.createMatchParty(1L, user, Response.UNDEFINED),
                MatchPartyTestUtils.createMatchParty(2L, opponentUser, Response.UNDEFINED)
        );
        Set<Match> matches = new HashSet<>();
        matches.add(match);

        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.ILE, RelationType.Code.DUAL)).thenReturn(sleSociotype);
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.LSE, RelationType.Code.DUAL)).thenReturn(eseSociotype);
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.SEE, RelationType.Code.DUAL)).thenReturn(iliSociotype);
        when(matchRepository.findForPossibleRemoval(user)).thenReturn(matches);

        userService.setUserSociotypes(user, newSociotypes);
        verify(matchRepository, times(1)).findForPossibleRemoval(user);
        verify(matchRepository, times(1)).delete(match);
        verify(userRepository, times(1)).save(user);
    }

    /**
     * Users were linked by two sociotypes, now first user updates his sociotypes so that now
     * they are linked only by one.
     * Match must not be removed in this case.
     */
    @Test
    public void testSetUserSociotypes_validMatch() throws Exception {
        Sociotype sleSociotype = new Sociotype.Builder().code1(Sociotype.Code1.SLE).build();
        Sociotype sliSociotype = new Sociotype.Builder().code1(Sociotype.Code1.SLI).build();
        Sociotype iliSociotype = new Sociotype.Builder().code1(Sociotype.Code1.ILI).build();

        User opponentUser = UserTestUtils.createUser(2L);
        Set<Sociotype> opponentSociotypes = new HashSet<>();
        opponentSociotypes.add(sleSociotype);
        opponentSociotypes.add(sliSociotype);
        opponentUser.setSociotypes(opponentSociotypes);

        User user = UserTestUtils.createUser();
        Sociotype oldSociotype1 = new Sociotype.Builder().code1(Sociotype.Code1.ILE).build();
        Sociotype oldsociotype2 = new Sociotype.Builder().code1(Sociotype.Code1.LSE).build();
        Set<Sociotype> oldSociotypes = new HashSet<>();
        oldSociotypes.add(oldSociotype1);
        oldSociotypes.add(oldsociotype2);
        user.setSociotypes(oldSociotypes);

        Sociotype newSociotype1 = new Sociotype.Builder().code1(Sociotype.Code1.LSE).build();
        Sociotype newSociotype2 = new Sociotype.Builder().code1(Sociotype.Code1.SEE).build();
        Set<Sociotype> newSociotypes = new HashSet<>();
        newSociotypes.add(newSociotype1);
        newSociotypes.add(newSociotype2);

        Match match = MatchTestUtils.createMatch(1L,
                MatchPartyTestUtils.createMatchParty(1L, user, Response.UNDEFINED),
                MatchPartyTestUtils.createMatchParty(2L, opponentUser, Response.UNDEFINED)
        );
        Set<Match> matches = new HashSet<>();
        matches.add(match);

        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.LSE, RelationType.Code.DUAL)).thenReturn(sliSociotype);
        when(sociotypeRepository.findOppositeByRelationType(Sociotype.Code1.SEE, RelationType.Code.DUAL)).thenReturn(iliSociotype);
        when(matchRepository.findForPossibleRemoval(user)).thenReturn(matches);

        userService.setUserSociotypes(user, newSociotypes);
        verify(matchRepository, times(1)).findForPossibleRemoval(user);
        verify(matchRepository, never()).delete(match);
        verify(userRepository, times(1)).save(user);
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

    @Test
    public void deleteUserPhoto_invalidCount() throws Exception {
        User user = UserTestUtils.createUser();
        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo());
        user.setPhotos(photos);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        try {
            userService.deleteUserPhoto(1L, 1L);
            fail();
        } catch (IllegalStateException ise) {
            assertEquals("User must have at least one photo", ise.getMessage());
        }
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void deleteUserPhoto() throws Exception {
        User user = UserTestUtils.createUser();
        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo());
        Photo photo = new Photo();
        photos.add(photo);
        user.setPhotos(photos);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(photoRepository.findUserPhoto(1L, 1L)).thenReturn(Optional.of(photo));
        userService.deleteUserPhoto(1L, 1L);
        verify(userRepository, times(1)).save(user);
        assertEquals(1, user.getPhotos().size());
    }
}