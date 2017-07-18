package lt.dualpair.server.geo.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Geometry {

    @JsonProperty("location_type")
    private String locationType;

    public String getLocationType() {
        return locationType;
    }

}
