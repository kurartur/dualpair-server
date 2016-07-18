package com.artur.dualpair.server.domain.model.geo;

import org.springframework.stereotype.Component;

@Component
public class GoogleLocationProvider extends LocationProvider {

    @Override
    public Location getLocation(double latitude, double longitude) {
        return new Location(54.63, 25.32, "LT", "Vilnius");
    }
}
