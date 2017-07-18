package lt.dualpair.server.geo.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

    @JsonProperty("address_components")
    private AddressComponent[] addressComponents;

    @JsonProperty("formatted_address")
    private String formattedAddress;

    private Geometry geometry;

    private String[] types;

    @JsonProperty("place_id")
    private String placeId;

    public AddressComponent[] getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(AddressComponent[] addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

}
