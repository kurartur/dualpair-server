package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.user.Response;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserResponse;
import lt.dualpair.core.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.match.OpponentUserResourceAssembler;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserResponseResourceAssemblerTest {

    private UserResponseResourceAssembler userResponseResourceAssembler;
    private OpponentUserResourceAssembler opponentUserResourceAssembler = mock(OpponentUserResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        userResponseResourceAssembler = new UserResponseResourceAssembler(opponentUserResourceAssembler);
    }

    @Test
    public void toResource() {
        UserResponse entity = new UserResponse();
        User user = UserTestUtils.createUser(1L);
        entity.setToUser(user);
        entity.setResponse(Response.YES);
        entity.setMatch(new Match());
        UserResource userResource = new UserResource();
        when(opponentUserResourceAssembler.toResource(new OpponentUserResourceAssembler.AssemblingContext(user, true))).thenReturn(userResource);
        UserResponseResource result = userResponseResourceAssembler.toResource(entity);
        assertEquals("Y", result.getResponse());
        assertEquals(userResource, result.getUser());
    }
}