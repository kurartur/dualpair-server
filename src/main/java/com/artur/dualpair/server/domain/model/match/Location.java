package com.artur.dualpair.server.domain.model.match;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Location {

    private double latitude;

    private double longitude;

    @Column(name = "country_code")
    private String countryCode;

    private Location() {}

    public Location(double latitude, double longitude, String countryCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
