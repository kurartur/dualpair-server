package lt.dualpair.server.geo;

import lt.dualpair.core.location.Location;
import lt.dualpair.core.location.LocationProvider;
import lt.dualpair.core.location.LocationProviderException;
import lt.dualpair.server.geo.google.GoogleLocationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MultipleServiceLocationProvider extends LocationProvider {

    private GoogleLocationProvider googleLocationProvider;

    @Override
    public Location getLocation(double latitude, double longitude) throws LocationProviderException {
        return googleLocationProvider.getLocation(latitude, longitude);
    }

    @Autowired
    public void setGoogleLocationProvider(GoogleLocationProvider googleLocationProvider) {
        this.googleLocationProvider = googleLocationProvider;
    }
}
