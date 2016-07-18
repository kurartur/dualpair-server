package com.artur.dualpair.server.domain.model.geo;

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
