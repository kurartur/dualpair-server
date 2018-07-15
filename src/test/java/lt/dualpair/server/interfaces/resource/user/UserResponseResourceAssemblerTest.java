package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.user.Response;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserResponse;
import lt.dualpair.core.user.UserTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserResponseResourceAssemblerTest {

    private UserResponseResourceAssembler userResponseResourceAssembler;
    private UserResourceAssembler userResourceAssembler = mock(UserResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        userResponseResourceAssembler = new UserResponseResourceAssembler(userResourceAssembler);
    }

    @Test
    public void toResource() {
        UserResponse entity = new UserResponse();
        User user = UserTestUtils.createUser(1L);
        entity.setUser(UserTestUtils.createUser(2L));
        entity.setToUser(user);
        entity.setResponse(Response.YES);
        entity.setMatch(new Match());
        Date date = new Date();
        entity.setDate(date);
        UserResource userResource = new UserResource();
        when(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(user, true, false))).thenReturn(userResource);
        UserResponseResource result = userResponseResourceAssembler.toResource(entity);
        assertEquals("Y", result.getResponse());
        assertEquals(userResource, result.getUser());
        assertTrue(result.isMatch());
        assertNotNull(result.getLink("match"));
        assertEquals(date, result.getDate());
    }

    @Test
    public void toResource_whenNoMatch_matchIsFalse() {
        UserResponse entity = new UserResponse();
        User user = UserTestUtils.createUser(1L);
        entity.setToUser(user);
        entity.setResponse(Response.YES);
        UserResource userResource = new UserResource();
        when(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(user, false, false))).thenReturn(userResource);
        UserResponseResource result = userResponseResourceAssembler.toResource(entity);
        assertEquals("Y", result.getResponse());
        assertEquals(userResource, result.getUser());
        assertFalse(result.isMatch());
        assertNull(result.getLink("match"));
    }
}