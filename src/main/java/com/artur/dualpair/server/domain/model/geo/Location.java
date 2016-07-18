package com.artur.dualpair.server.domain.model.geo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Location implements Serializable {

    private Double latitude;

    private Double longitude;

    @Column(name = "country_code")
    private String countryCode;

    private String city;

    private Location() {}

    public Location(Double latitude, Double longitude, String countryCode, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = countryCode;
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCity() {
        return city;
    }
}
