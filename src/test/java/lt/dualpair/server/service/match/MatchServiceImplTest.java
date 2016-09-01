package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.match.*;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocationTestUtils;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.service.user.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import static lt.dualpair.server.domain.model.match.MatchPartyTestUtils.createMatchParty;
import static lt.dualpair.server.domain.model.user.UserTestUtils.createUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class MatchServiceImplTest {

    private MatchServiceImpl matchService = new MatchServiceImpl();
    private DefaultMatchFinder defaultMatchFinder = mock(DefaultMatchFinder.class);
    private RepositoryMatchFinder repositoryMatchFinder = mock(RepositoryMatchFinder.class);
    private UserServiceImpl userService = mock(UserServiceImpl.class);
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private MatchRequestValidator matchRequestValidator = mock(MatchRequestValidator.class);
    private NotificationSender notificationSender = mock(NotificationSender.class);

    @Before
    public void setUp() throws Exception {
        matchService.setDefaultMatchFinder(defaultMatchFinder);
        matchService.setRepositoryMatchFinder(repositoryMatchFinder);
        matchService.setUserService(userService);
        matchService.setMatchRepository(matchRepository);
        matchService.setMatchRequestValidator(matchRequestValidator);
        matchService.setNotificationSender(notificationSender);
    }

    @Test
    public void testNextFor_hasMatchInRepo() throws Exception {
        Match match = new Match();
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(10);
        searchParameters.setMaxAge(20);
        searchParameters.setSearchFemale(true);
        user.setSearchParameters(searchParameters);
        user.addLocation(UserLocationTestUtils.createUserLocation(10, "LT"));
        doReturn(match).when(repositoryMatchFinder).findOne(any(MatchRequest.class));
        when(userService.loadUserById(1L)).thenReturn(user);
        Match resultMatch = matchService.nextFor(1L, null);
        verify(userService, times(1)).loadUserById(1L);
        verifyNoMoreInteractions(defaultMatchFinder);
        verify(matchRepository, times(1)).save(match);
        verify(matchRequestValidator, times(1)).validateMatchRequest(user, searchParameters);
        assertEquals(match, resultMatch);
    }

    @Test
    public void testNextFor_hasMatchInDefault() throws Exception {
        Match match = new Match();
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(10);
        searchParameters.setMaxAge(20);
        searchParameters.setSearchFemale(true);
        user.setSearchParameters(searchParameters);
        user.addLocation(UserLocationTestUtils.createUserLocation(10, "LT"));
        doReturn(match).when(defaultMatchFinder).findOne(any(MatchRequest.class));
        when(userService.loadUserById(1L)).thenReturn(user);
        Match resultMatch = matchService.nextFor(1L, null);
        verify(userService, times(1)).loadUserById(1L);
        verify(repositoryMatchFinder, times(1)).findOne(any(MatchRequest.class));
        verify(matchRepository, times(1)).save(match);
        verify(matchRequestValidator, times(1)).validateMatchRequest(user, searchParameters);
        assertEquals(match, resultMatch);
    }

    @Test
    public void testNextFor_validationException() throws Exception {
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        user.setSearchParameters(searchParameters);
        doThrow(new MatchRequestException("Error")).when(matchRequestValidator).validateMatchRequest(user, searchParameters);
        when(userService.loadUserById(1L)).thenReturn(user);
        try {
            matchService.nextFor(1L, null);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Error", mre.getMessage());
            verify(userService, times(1)).loadUserById(1L);
            verify(repositoryMatchFinder, times(0)).findOne(any(MatchRequest.class));
            verify(defaultMatchFinder, times(0)).findOne(any(MatchRequest.class));
            verify(matchRepository, times(0)).save(any(Match.class));
            verify(matchRequestValidator, times(1)).validateMatchRequest(user, searchParameters);
        }
    }

    @Test
    public void testSendMutualMatchNotifications() throws Exception {
        MatchParty matchParty1 = createMatchParty(1L, createUser(1L), Response.YES);
        MatchParty matchParty2 = createMatchParty(2L, createUser(2L), Response.YES);
        Match match = MatchTestUtils.createMatch(1L, matchParty1, matchParty2);
        matchService.sendMutualMatchNotifications(match);
        verify(notificationSender, times(2)).sendNotification(any(Notification.class));
    }
}