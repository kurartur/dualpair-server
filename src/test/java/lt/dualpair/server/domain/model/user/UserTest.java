package lt.dualpair.server.domain.model.user;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserTest {

    @Test
    public void testAddLocation() throws Exception {
        User user = new User();
        UserLocation userLocation = new UserLocation(user, null, null, null, null);
        user.addLocation(userLocation, 1);
        assertEquals(1, user.getLocations().size());
    }

    @Test
    public void testAddLocation_oldLocationsDeleted() throws Exception {
        User user = new User();
        UserLocation userLocation1 = new UserLocation(user, null, null, null, null);
        UserLocation userLocation2 = new UserLocation(user, null, null, null, null);
        UserLocation userLocation3 = new UserLocation(user, null, null, null, null);
        UserLocation userLocation4 = new UserLocation(user, null, null, null, null);
        UserLocation userLocation5 = new UserLocation(user, null, null, null, null);
        UserLocation userLocation6 = new UserLocation(user, null, null, null, null);
        user.addLocation(userLocation1, 10);
        user.addLocation(userLocation2, 10);
        user.addLocation(userLocation3, 10);
        user.addLocation(userLocation4, 10);
        user.addLocation(userLocation5, 10);
        user.addLocation(userLocation6, 3);
        assertEquals(3, user.getLocations().size());
        List<UserLocation> locations = user.getLocations();
        assertTrue(locations.containsAll(Arrays.asList(userLocation4, userLocation5, userLocation6)));
    }
}