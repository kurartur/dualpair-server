package lt.dualpair.server.service.match;

import lt.dualpair.server.domain.model.match.*;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.service.user.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import static lt.dualpair.server.domain.model.match.MatchPartyTestUtils.createMatchParty;
import static lt.dualpair.server.domain.model.user.UserTestUtils.createUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MatchServiceImplTest {

    private MatchServiceImpl matchService = new MatchServiceImpl();
    private DefaultMatchFinder defaultMatchFinder = mock(DefaultMatchFinder.class);
    private RepositoryMatchFinder repositoryMatchFinder = mock(RepositoryMatchFinder.class);
    private UserServiceImpl userService = mock(UserServiceImpl.class);
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private NotificationSender notificationSender = mock(NotificationSender.class);

    @Before
    public void setUp() throws Exception {
        matchService.setDefaultMatchFinder(defaultMatchFinder);
        matchService.setRepositoryMatchFinder(repositoryMatchFinder);
        matchService.setUserService(userService);
        matchService.setMatchRepository(matchRepository);
        matchService.setNotificationSender(notificationSender);
    }

    @Test
    public void testNextFor_hasMatchInRepo() throws Exception {
        MatchRequest matchRequest = new TestMatchRequestBuilder().build();
        Match match = new Match();
        doReturn(match).when(repositoryMatchFinder).findOne(matchRequest);
        Match resultMatch = matchService.nextFor(matchRequest);
        verifyNoMoreInteractions(defaultMatchFinder);
        verify(matchRepository, times(1)).save(match);
        assertEquals(match, resultMatch);
    }

    @Test
    public void testNextFor_hasMatchInDefault() throws Exception {
        MatchRequest matchRequest = new TestMatchRequestBuilder().build();
        Match match = new Match();
        doReturn(match).when(defaultMatchFinder).findOne(matchRequest);
        Match resultMatch = matchService.nextFor(matchRequest);
        verify(repositoryMatchFinder, times(1)).findOne(any(MatchRequest.class));
        verify(matchRepository, times(1)).save(match);
        assertEquals(match, resultMatch);
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