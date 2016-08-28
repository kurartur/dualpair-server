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
    private BasicMatchPartyResourceAssembler basicMatchPartyResourceAssembler = mock(BasicMatchPartyResourceAssembler.class);
    private FullMatchPartyResourceAssembler fullMatchPartyResourceAssembler = mock(FullMatchPartyResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        matchResourceAssembler.setBasicMatchPartyResourceAssembler(basicMatchPartyResourceAssembler);
        matchResourceAssembler.setFullMatchPartyResourceAssembler(fullMatchPartyResourceAssembler);
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
        FullMatchPartyResource fullMatchPartyResource = new FullMatchPartyResource();
        BasicMatchPartyResource basicMatchPartyResource = new BasicMatchPartyResource();
        when(fullMatchPartyResourceAssembler.toResource(matchParty2)).thenReturn(fullMatchPartyResource);
        when(basicMatchPartyResourceAssembler.toResource(matchParty1)).thenReturn(basicMatchPartyResource);
        MatchResource matchResource = matchResourceAssembler.toResource(new UserAwareMatch(user, match));
        assertEquals((Long)1L, matchResource.getMatchId());
        assertEquals(fullMatchPartyResource, matchResource.getOpponent());
        assertEquals(basicMatchPartyResource, matchResource.getUser());
        assertEquals((Integer)10, matchResource.getDistance());
    }
}