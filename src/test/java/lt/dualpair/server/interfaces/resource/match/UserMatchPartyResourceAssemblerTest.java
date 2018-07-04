package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchParty;
import lt.dualpair.core.user.User;
import lt.dualpair.server.interfaces.resource.BaseResourceAssemblerTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserMatchPartyResourceAssemblerTest extends BaseResourceAssemblerTest {

    private UserMatchPartyResourceAssembler userMatchPartyResourceAssembler = new UserMatchPartyResourceAssembler();

    @Test
    public void testToResource() throws Exception {
        User user = new User();
        user.setId(1L);
        Match match = new Match();
        match.setId(10L);
        MatchParty matchParty = new MatchParty();
        matchParty.setId(100L);
        matchParty.setUser(user);
        matchParty.setMatch(match);
        UserMatchPartyResource matchPartyResource = userMatchPartyResourceAssembler.toResource(matchParty);
        assertTrue(matchPartyResource.getLink("user").getHref().endsWith("api/me"));
        assertEquals((Long)100L, matchPartyResource.getPartyId());
    }

}