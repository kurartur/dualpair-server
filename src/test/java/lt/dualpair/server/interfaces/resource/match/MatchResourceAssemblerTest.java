package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.core.match.*;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.BaseResourceAssemblerTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchResourceAssemblerTest extends BaseResourceAssemblerTest {

    private MatchResourceAssembler matchResourceAssembler = new MatchResourceAssembler();
    private UserMatchPartyResourceAssembler userMatchPartyResourceAssembler = mock(UserMatchPartyResourceAssembler.class);
    private OpponentMatchPartyResourceAssembler opponentMatchPartyResourceAssembler = mock(OpponentMatchPartyResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        matchResourceAssembler.setUserMatchPartyResourceAssembler(userMatchPartyResourceAssembler);
        matchResourceAssembler.setOpponentMatchPartyResourceAssembler(opponentMatchPartyResourceAssembler);
    }

    @Test
    public void testToResource() throws Exception {
        User user = UserTestUtils.createUser(1L);
        MatchParty matchParty1 = MatchPartyTestUtils.createMatchParty(10L, user);
        MatchParty matchParty2 = MatchPartyTestUtils.createMatchParty(11L, UserTestUtils.createUser(2L));
        Match match = MatchTestUtils.createMatch(100L, matchParty1, matchParty2);

        OpponentMatchPartyResource opponentMatchPartyResource = new OpponentMatchPartyResource();
        when(opponentMatchPartyResourceAssembler.toResource(matchParty2)).thenReturn(opponentMatchPartyResource);
        UserMatchPartyResource userMatchPartyResource = new UserMatchPartyResource();
        when(userMatchPartyResourceAssembler.toResource(matchParty1)).thenReturn(userMatchPartyResource);

        MatchResource matchResource = matchResourceAssembler.toResource(new UserAwareMatch(user, match));
        assertEquals((Long)100L, matchResource.getMatchId());
        assertEquals(opponentMatchPartyResource, matchResource.getOpponent());
        assertEquals(userMatchPartyResource, matchResource.getUser());
    }
}