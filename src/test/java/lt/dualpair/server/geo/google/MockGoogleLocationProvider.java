package lt.dualpair.server.geo.google;

import lt.dualpair.core.location.Location;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("it")
public class MockGoogleLocationProvider extends GoogleLocationProvider {

    @Override
    public Location getLocation(double latitude, double longitude) {
        return new Location(latitude, longitude, "LT", "Vilnius");
    }

}