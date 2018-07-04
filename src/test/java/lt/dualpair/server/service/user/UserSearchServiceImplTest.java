package lt.dualpair.server.service.user;

import lt.dualpair.core.user.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserSearchServiceImplTest {

    private UserSearchServiceImpl service = new UserSearchServiceImpl();
    private DefaultUserFinder userFinder = mock(DefaultUserFinder.class);
    private FakeUserFinder fakeUserFinder = mock(FakeUserFinder.class);

    @Before
    public void setUp() throws Exception {
        service.setUserFinder(userFinder);
        service.setFakeUserFinder(fakeUserFinder);
        service.setFakeMatches(false);
    }

    @Test
    public void findOne_whenRequestIsNull_exceptionThrown() throws Exception {
        try {
            service.findOne(null);
            fail();
        } catch (IllegalArgumentException iae) {
        }
    }

    @Test
    public void findOne_whenNothingFound_emptyReturned() throws Exception {
        UserRequest request = new TestUserRequestBuilder().build();
        when(userFinder.findOne(request)).thenReturn(Optional.empty());
        when(fakeUserFinder.findOne(request)).thenReturn(Optional.of(new User()));
        assertFalse(service.findOne(request).isPresent());
        verify(fakeUserFinder, never()).findOne(any(UserRequest.class));
    }

    @Test
    public void findOne_whenNothingFoundAndFakeEnabled_fakeFound() throws Exception {
        service.setFakeMatches(true);
        UserRequest request = new TestUserRequestBuilder().build();
        when(userFinder.findOne(request)).thenReturn(Optional.empty());
        User user = new User();
        when(fakeUserFinder.findOne(request)).thenReturn(Optional.of(user));
        Optional<User> result = service.findOne(request);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    public void findOne() throws Exception {
        UserRequest request = new TestUserRequestBuilder().build();
        User user = UserTestUtils.createUser();
        when(userFinder.findOne(request)).thenReturn(Optional.of(user));
        Optional<User> result = service.findOne(request);
        assertEquals(user, result.get());
    }

}