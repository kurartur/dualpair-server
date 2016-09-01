package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.domain.model.user.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchResourceAssemblerTest {

    private MatchResourceAssembler matchResourceAssembler = new MatchResourceAssembler();
    private UserMatchPartyResourceAssembler userMatchPartyResourceAssembler = mock(UserMatchPartyResourceAssembler.class);
    private OpponentMatchPartyResourceAssembler opponentMatchPartyResourceAssembler = mock(OpponentMatchPartyResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        matchResourceAssembler.setUserMatchPartyResourceAssembler(userMatchPartyResourceAssembler);
        matchResourceAssembler.setOpponentMatchPartyResourceAssembler(opponentMatchPartyResourceAssembler);
    }

    @Test
    @Ignore // TODO move to integration test?
    public void testToResource() throws Exception {
        User user = new User();
        user.setId(1L);
        User opponent = new User();
        opponent.setId(2L);
        MatchParty matchParty1 = new MatchParty();
        matchParty1.setUser(user);
        MatchParty matchParty2 = new MatchParty();
        matchParty2.setUser(opponent);
        Match match = new Match();
        match.setId(1L);
        match.setDistance(10);
        OpponentMatchPartyResource opponentMatchPartyResource = new OpponentMatchPartyResource();
        UserMatchPartyResource userMatchPartyResource = new UserMatchPartyResource();
        when(opponentMatchPartyResourceAssembler.toResource(matchParty2)).thenReturn(opponentMatchPartyResource);
        when(userMatchPartyResourceAssembler.toResource(matchParty1)).thenReturn(userMatchPartyResource);
        MatchResource matchResource = matchResourceAssembler.toResource(new UserAwareMatch(user, match));
        assertEquals((Long)1L, matchResource.getMatchId());
        assertEquals(opponentMatchPartyResource, matchResource.getOpponent());
        assertEquals(userMatchPartyResource, matchResource.getUser());
        assertEquals((Integer)10, matchResource.getDistance());
    }
}