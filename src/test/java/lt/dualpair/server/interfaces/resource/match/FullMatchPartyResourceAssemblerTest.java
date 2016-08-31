package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.Response;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FullMatchPartyResourceAssemblerTest {

    private FullMatchPartyResourceAssembler fullMatchPartyResourceAssembler = new FullMatchPartyResourceAssembler();
    private UserResourceAssembler userResourceAssembler = mock(UserResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        fullMatchPartyResourceAssembler.setUserResourceAssembler(userResourceAssembler);
    }

    @Test
    @Ignore // TODO move to integration test?
    public void testToResource() throws Exception {
        User user = new User();
        Match match = new Match();
        match.setId(1L);
        MatchParty matchParty = new MatchParty();
        matchParty.setUser(user);
        matchParty.setMatch(match);
        matchParty.setResponse(Response.YES);
        UserResource userResource = new UserResource();
        when(userResourceAssembler.toResource(user)).thenReturn(userResource);
        FullMatchPartyResource matchPartyResource = fullMatchPartyResourceAssembler.toResource(matchParty);
        assertEquals(userResource, matchPartyResource.getUser());
        assertTrue(matchPartyResource.getLink("match").getHref().endsWith("/match/1"));
        assertEquals("YES", matchPartyResource.getResponse());
    }
}