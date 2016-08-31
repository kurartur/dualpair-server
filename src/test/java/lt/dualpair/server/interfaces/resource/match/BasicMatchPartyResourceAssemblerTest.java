package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.Response;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BasicMatchPartyResourceAssemblerTest {

    private BasicMatchPartyResourceAssembler basicMatchPartyResourceAssembler = new BasicMatchPartyResourceAssembler();

    @Test
    @Ignore // TODO move to integration test?
    public void testToResource() throws Exception {
        User user = new User();
        user.setId(1L);
        Match match = new Match();
        match.setId(1L);
        MatchParty matchParty = new MatchParty();
        matchParty.setUser(user);
        matchParty.setMatch(match);
        matchParty.setResponse(Response.YES);
        UserResource userResource = new UserResource();
        BasicMatchPartyResource matchPartyResource = basicMatchPartyResourceAssembler.toResource(matchParty);
        assertTrue(matchPartyResource.getLink("user").getHref().endsWith("api/me"));
        assertTrue(matchPartyResource.getLink("match").getHref().endsWith("api/match/1"));
        assertEquals("YES", matchPartyResource.getResponse());
    }

}