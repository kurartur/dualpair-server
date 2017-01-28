package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.MatchPartyTestUtils;
import lt.dualpair.server.domain.model.match.MatchTestUtils;
import lt.dualpair.server.domain.model.match.Response;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.BaseResourceAssemblerTest;
import lt.dualpair.server.interfaces.resource.match.OpponentUserResourceAssembler.AssemblingContext;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OpponentMatchPartyResourceAssemblerTest extends BaseResourceAssemblerTest {

    private OpponentMatchPartyResourceAssembler opponentMatchPartyResourceAssembler = new OpponentMatchPartyResourceAssembler();
    private OpponentUserResourceAssembler userResourceAssembler = mock(OpponentUserResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        opponentMatchPartyResourceAssembler.setOpponentUserResourceAssembler(userResourceAssembler);
    }

    @Test
    public void testToResource() throws Exception {
        User user = new User();
        MatchParty matchParty1 = MatchPartyTestUtils.createMatchParty(10L, user, Response.NO);
        MatchParty matchParty2 = MatchPartyTestUtils.createMatchParty(11L, user, Response.NO);
        MatchTestUtils.createMatch(1L, matchParty1, matchParty2);
        UserResource userResource = new UserResource();
        doReturn(userResource).when(userResourceAssembler).toResource(any(AssemblingContext.class));
        OpponentMatchPartyResource matchPartyResource = opponentMatchPartyResourceAssembler.toResource(matchParty1);
        assertEquals((Long)10L, matchPartyResource.getPartyId());
        assertEquals(userResource, matchPartyResource.getUser());

        ArgumentCaptor<AssemblingContext> assemblingContextArgumentCaptor = ArgumentCaptor.forClass(AssemblingContext.class);
        verify(userResourceAssembler, times(1)).toResource(assemblingContextArgumentCaptor.capture());
        AssemblingContext context = assemblingContextArgumentCaptor.getValue();
        assertEquals(user, context.getUser());
        assertEquals(false, context.isMutualMatch());
        assertEquals(null, matchPartyResource.getResponse());
    }

    @Test
    public void testToResource_mutual() throws Exception {
        User user = new User();
        MatchParty matchParty1 = MatchPartyTestUtils.createMatchParty(10L, user, Response.YES);
        MatchParty matchParty2 = MatchPartyTestUtils.createMatchParty(11L, user, Response.YES);
        MatchTestUtils.createMatch(1L, matchParty1, matchParty2);
        OpponentMatchPartyResource matchPartyResource = opponentMatchPartyResourceAssembler.toResource(matchParty1);

        ArgumentCaptor<AssemblingContext> assemblingContextArgumentCaptor = ArgumentCaptor.forClass(AssemblingContext.class);
        verify(userResourceAssembler, times(1)).toResource(assemblingContextArgumentCaptor.capture());
        AssemblingContext context = assemblingContextArgumentCaptor.getValue();
        assertEquals(user, context.getUser());
        assertEquals(true, context.isMutualMatch());
        assertEquals(Response.YES.name(), matchPartyResource.getResponse());
    }
}