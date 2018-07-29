package lt.dualpair.server.service.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchParty;
import lt.dualpair.core.match.MatchPartyTestUtils;
import lt.dualpair.core.match.MatchTestUtils;
import lt.dualpair.core.user.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserMatchServiceImplTest {

    private UserMatchServiceImpl service;
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private UserResponseRepository userResponseRepository = mock(UserResponseRepository.class);

    @Before
    public void setUp() throws Exception {
        service = new UserMatchServiceImpl(matchRepository, userResponseRepository);
    }

    @Test
    public void unmatch_whenNullArgs_exceptionThrown() {
        try {
            service.unmatch(null, null);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            service.unmatch(1L, null);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            service.unmatch(null, 1L);
            fail();
        } catch (IllegalArgumentException iae) {}
    }

    @Test
    public void unmatch_whenMatchNotFound_exceptionThrown() {
        when(matchRepository.findOneByUser(1L, 2L)).thenReturn(Optional.empty());
        try {
            service.unmatch(2L, 1L);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Match with id 2 and user id 1 not found", iae.getMessage());
        }
    }

    @Test
    public void unmatch() {
        User user1 = UserTestUtils.createUser(1L);
        User user2 = UserTestUtils.createUser(2L);
        MatchParty mp1 = MatchPartyTestUtils.createMatchParty(1L, user1);
        MatchParty mp2 = MatchPartyTestUtils.createMatchParty(2L, user2);
        Match match = MatchTestUtils.createMatch(1L, mp1, mp2);
        when(matchRepository.findOneByUser(1L, 3L)).thenReturn(Optional.of(match));
        UserResponse userResponse = new UserResponse();
        userResponse.setUser(user1);
        userResponse.setToUser(user2);
        Date responseDate = new Date(50);
        userResponse.setDate(responseDate);
        userResponse.setResponse(Response.YES);
        userResponse.setMatch(match);
        when(userResponseRepository.findByParties(1L, 2L)).thenReturn(Optional.of(userResponse));
        UserResponse opponentResponse = new UserResponse();
        opponentResponse.setUser(user2);
        opponentResponse.setToUser(user1);
        opponentResponse.setDate(responseDate);
        opponentResponse.setResponse(Response.YES);
        opponentResponse.setMatch(match);
        when(userResponseRepository.findByParties(2L, 1L)).thenReturn(Optional.of(opponentResponse));
        service.unmatch(3L, 1L);
        verify(matchRepository, times(1)).delete(3L);
        ArgumentCaptor<UserResponse> userResponseCaptor = ArgumentCaptor.forClass(UserResponse.class);
        verify(userResponseRepository, times(2)).save(userResponseCaptor.capture());
        UserResponse savedUserResponse = userResponseCaptor.getAllValues().get(0);
        assertEquals(user1, savedUserResponse.getUser());
        assertEquals(Response.NO, savedUserResponse.getResponse());
        assertNotEquals(responseDate, savedUserResponse.getDate());
        assertNull(savedUserResponse.getMatch());
        UserResponse savedOpponentResponse = userResponseCaptor.getAllValues().get(1);
        assertEquals(user2, savedOpponentResponse.getUser());
        assertEquals(Response.YES, savedOpponentResponse.getResponse());
        assertEquals(responseDate, savedOpponentResponse.getDate());
        assertNull(savedUserResponse.getMatch());
    }
}