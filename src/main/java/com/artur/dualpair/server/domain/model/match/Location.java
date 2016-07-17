package com.artur.dualpair.server.domain.model.match;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Location implements Serializable {

    private Double latitude;

    private Double longitude;

    @Column(name = "country_code")
    private String countryCode;

    private Location() {}

    public Location(Double latitude, Double longitude, String countryCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = countryCode;
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

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
