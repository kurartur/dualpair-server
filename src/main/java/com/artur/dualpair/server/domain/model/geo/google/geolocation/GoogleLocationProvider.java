package com.artur.dualpair.server.domain.model.geo.google.geolocation;

import com.artur.dualpair.server.domain.model.geo.Location;
import com.artur.dualpair.server.domain.model.geo.LocationProvider;
import com.artur.dualpair.server.domain.model.geo.LocationProviderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("!it")
public class GoogleLocationProvider extends LocationProvider {

    private static final String ENDPOINT_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    @Value("googleApiKey")
    protected String apiKey;

    @Override
    public Location getLocation(double latitude, double longitude) throws LocationProviderException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("latlng", latitude + "," + longitude);
        parameters.put("key", apiKey);
        try {
            ResponseEntity<Response> responseEntity = getRestTemplate().getForEntity(ENDPOINT_URL + "?latlng={latlng}&key={key}", Response.class, parameters);
            Response response = responseEntity.getBody();
            if (!response.isOk()) {
                throw new LocationProviderException(response.getErrorMessage());
            } else {
                String countryCode = response.getCountryCode();
                String city = response.getCity();
                return new Location(latitude, longitude, countryCode, city);
            }
        } catch (Exception e) {
            throw new LocationProviderException("Unable to retrieve location: " + e.getMessage(), e);
        }
    }

    protected RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
            InetSocketAddress address = new InetSocketAddress(System.getProperty("http.proxyHost"), new Integer(System.getProperty("http.proxyPort")));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
            factory.setProxy(proxy);
        }
        return new RestTemplate(factory);
    }

}
