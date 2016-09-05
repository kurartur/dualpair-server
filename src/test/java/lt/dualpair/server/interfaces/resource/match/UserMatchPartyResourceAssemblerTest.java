package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.Response;
import lt.dualpair.server.domain.model.user.User;
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
        matchParty.setResponse(Response.YES);
        UserMatchPartyResource matchPartyResource = userMatchPartyResourceAssembler.toResource(matchParty);
        assertTrue(matchPartyResource.getLink("user").getHref().endsWith("api/me"));
        assertEquals("YES", matchPartyResource.getResponse());
        assertEquals((Long)100L, matchPartyResource.getPartyId());
    }

}