package lt.dualpair.server.domain.model.geo.google.geolocation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class Response {

    private Collection<Result> results;

    private String status;

    @JsonProperty("error_message")
    private String errorMessage;

    public Collection<Result> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public String getCountryCode() {
        for (Result result : results) {
            for (AddressComponent addressComponent : result.getAddressComponents()) {
                if (addressComponent.isTypeCountry()) {
                    return addressComponent.getShortName();
                }
            }
        }
        return null;
    }

    public String getCity() {
        for (Result result : results) {
            for (AddressComponent addressComponent : result.getAddressComponents()) {
                if (addressComponent.isTypeLocality()) {
                    return addressComponent.getShortName();
                }
            }
        }
        return null;
    }

    public boolean isOk() {
        return "OK".equals(status);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
