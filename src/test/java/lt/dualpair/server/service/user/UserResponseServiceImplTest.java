package lt.dualpair.server.service.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.user.*;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserResponseServiceImplTest {

    private UserResponseServiceImpl service;
    private UserRepository userRepository = mock(UserRepository.class);
    private UserResponseRepository userResponseRepository = mock(UserResponseRepository.class);
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private NotificationSender notificationSender = mock(NotificationSender.class);

    @Before
    public void setUp() throws Exception {
        service = new UserResponseServiceImpl(userRepository, userResponseRepository, matchRepository, notificationSender, false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserTestUtils.createUser(1L)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(UserTestUtils.createUser(2L)));
        when(userResponseRepository.findByParties(any(Long.class), any(Long.class))).thenReturn(Optional.empty());
    }

    @Test
    public void respond_whenOneOfParamsIsNull_exceptionThrown() {
        try {
            service.respond(null, null, null);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            service.respond(1L, null, null);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            service.respond(1L, 1L, null);
            fail();
        } catch (IllegalArgumentException iae) {}
    }

    @Test
    public void respond_responseIsSaved() {
        service.respond(1L, 2L, Response.YES);
        ArgumentCaptor<UserResponse> userResponseCaptor = ArgumentCaptor.forClass(UserResponse.class);
        verify(userResponseRepository, times(1)).save(userResponseCaptor.capture());
        UserResponse response = userResponseCaptor.getValue();
        assertEquals(new Long(1), response.getUser().getId());
        assertEquals(new Long(2), response.getToUser().getId());
        assertEquals(Response.YES, response.getResponse());
        assertNotNull(response.getDate());
    }

    @Test
    public void respond_whenRespondToThisUserExists_exceptionThrown() {
        when(userResponseRepository.findByParties(1L, 2L)).thenReturn(Optional.of(new UserResponse()));
        try {
            service.respond(1L, 2L, Response.YES);
            fail();
        } catch (IllegalStateException ise) {
            assertEquals("Response from user 1 to user 2 already exists", ise.getMessage());
        }
    }

    @Test
    public void respond_whenFakeMatchesEnabled_createResponseFromFake() {
        service = new UserResponseServiceImpl(userRepository, userResponseRepository, matchRepository, notificationSender, true);
        User toUser = UserTestUtils.createUser(2L);
        toUser.setDescription("Lorem ipsum FAKE");
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));

        service.respond(1L, 2L, Response.YES);

        ArgumentCaptor<UserResponse> userResponseCaptor = ArgumentCaptor.forClass(UserResponse.class);
        verify(userResponseRepository, times(2)).save(userResponseCaptor.capture());
        UserResponse fakeResponse = userResponseCaptor.getAllValues().get(1);
        assertEquals(new Long(2), fakeResponse.getUser().getId());
        assertEquals(new Long(1), fakeResponse.getToUser().getId());
    }

    @Test
    public void respond_whenFakeMatchesEnabledAndUserIsNotFake_responseFromFakeIsNotCreated() {
        service = new UserResponseServiceImpl(userRepository, userResponseRepository, matchRepository, notificationSender, true);

        service.respond(1L, 2L, Response.YES);

        ArgumentCaptor<UserResponse> userResponseCaptor = ArgumentCaptor.forClass(UserResponse.class);
        verify(userResponseRepository, times(1)).save(userResponseCaptor.capture());
    }

    @Test
    public void respond_whenNewMatch_matchIsCreated() {
        UserResponse opponentResponse = new UserResponse();
        opponentResponse.setResponse(Response.YES);
        when(userResponseRepository.findByParties(2L, 1L)).thenReturn(Optional.of(opponentResponse));

        service.respond(1L, 2L, Response.YES);

        ArgumentCaptor<Match> matchArgumentCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(1)).save(matchArgumentCaptor.capture());
        Match match = matchArgumentCaptor.getValue();
        assertEquals(new Long(1L), match.getMatchParty(1L).getUser().getId());
        assertEquals(new Long(2L), match.getMatchParty(2L).getUser().getId());
        assertNotNull(match.getDate());
    }

    @Test
    public void respond_whenNewMatch_notificationIsSent() {
        UserResponse opponentResponse = new UserResponse();
        opponentResponse.setResponse(Response.YES);
        when(userResponseRepository.findByParties(2L, 1L)).thenReturn(Optional.of(opponentResponse));

        service.respond(1L, 2L, Response.YES);

        verify(notificationSender, times(2)).sendNotification(any(Notification.class));
    }
}