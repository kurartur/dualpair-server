package lt.dualpair.server.domain.model.geo.google.geolocation;

import lt.dualpair.server.domain.model.geo.Location;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("it")
public class MockGoogleLocationProvider extends GoogleLocationProvider {

    @Override
    public Location getLocation(double latitude, double longitude) {
        return new Location(54.63, 25.32, "LT", "Vilnius");
    }

}