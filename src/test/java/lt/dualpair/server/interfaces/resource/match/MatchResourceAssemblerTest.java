package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.core.match.*;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.BaseResourceAssemblerTest;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchResourceAssemblerTest extends BaseResourceAssemblerTest {

    private MatchResourceAssembler matchResourceAssembler = new MatchResourceAssembler();
    private OpponentUserResourceAssembler opponentUserResourceAssembler = mock(OpponentUserResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        matchResourceAssembler.setOpponentUserResourceAssembler(opponentUserResourceAssembler);
    }

    @Test
    public void testToResource() throws Exception {
        User user = UserTestUtils.createUser(1L);
        User opponent = UserTestUtils.createUser(2L);
        MatchParty matchParty1 = MatchPartyTestUtils.createMatchParty(10L, user);
        MatchParty matchParty2 = MatchPartyTestUtils.createMatchParty(11L, opponent);
        Match match = MatchTestUtils.createMatch(100L, matchParty1, matchParty2);
        Date date = new Date();
        match.setDate(date);

        UserResource opponentResource = new UserResource();
        when(opponentUserResourceAssembler.toResource(new OpponentUserResourceAssembler.AssemblingContext(opponent, true))).thenReturn(opponentResource);

        MatchResource matchResource = matchResourceAssembler.toResource(new UserAwareMatch(user, match));
        assertEquals((Long)100L, matchResource.getMatchId());
        assertEquals(opponentResource, matchResource.getUser());
        assertEquals(date, matchResource.getDate());
    }
}