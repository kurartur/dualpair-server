package com.artur.dualpair.server.domain.model.geo;

public abstract class LocationProvider {

    public abstract Location getLocation(double latitude, double longitude) throws LocationProviderException;

}
