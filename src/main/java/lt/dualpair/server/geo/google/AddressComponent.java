package lt.dualpair.server.geo.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class AddressComponent {

    @JsonProperty("long_name") private String longName;
    @JsonProperty("short_name") private String shortName;
    private Collection<String> types;

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public Collection<String> getTypes() {
        return types;
    }

    public boolean isTypeCountry() {
        return types.contains("country");
    }

    public boolean isTypeLocality() {
        return types.contains("locality");
    }
}
