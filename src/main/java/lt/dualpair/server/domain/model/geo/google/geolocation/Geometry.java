package lt.dualpair.server.domain.model.geo.google.geolocation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Geometry {

    @JsonProperty("location_type")
    private String locationType;

    public String getLocationType() {
        return locationType;
    }

}
