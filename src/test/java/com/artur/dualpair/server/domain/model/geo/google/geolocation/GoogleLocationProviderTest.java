package com.artur.dualpair.server.domain.model.geo.google.geolocation;

import com.artur.dualpair.server.domain.model.geo.Location;
import com.artur.dualpair.server.domain.model.geo.LocationProviderException;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GoogleLocationProviderTest {

    @Test
    public void testGetLocation() throws Exception {
        RestTemplate restTemplate = new MockRestTemplate(
                createResponse(readFile("com/artur/dualpair/server/domain/model/geo/google/geolocation/response_ok.json"), HttpStatus.OK),
                new URI("https://maps.googleapis.com/maps/api/geocode/json?latlng=10.0,11.0&key=apiKey")
        );
        GoogleLocationProvider locationProvider = new MockGoogleLocationProvider(restTemplate);

        Location location = locationProvider.getLocation(10.0, 11.0);

        assertEquals("LT", location.getCountryCode());
        assertEquals("Vilnius", location.getCity());
        assertEquals(new Double(10.0), location.getLatitude());
        assertEquals(new Double(11.0), location.getLongitude());
    }

    @Test
    public void testGetLocation_requestError() throws Exception {
        RestTemplate restTemplate = new MockRestTemplate(
                createResponse("", HttpStatus.UNAUTHORIZED),
                new URI("https://maps.googleapis.com/maps/api/geocode/json?latlng=10.0,11.0&key=apiKey")
        );
        GoogleLocationProvider locationProvider = new MockGoogleLocationProvider(restTemplate);
        try {
            locationProvider.getLocation(10.0, 11.0);
            fail();
        } catch (LocationProviderException lpe) {
            assertEquals("Unable to retrieve location: 401 Unauthorized", lpe.getMessage());
        }
    }

    @Test
    public void testGetLocation_responseBodyStatusNotOk() throws Exception {
        RestTemplate restTemplate = new MockRestTemplate(
                createResponse(readFile("com/artur/dualpair/server/domain/model/geo/google/geolocation/response_nok.json"), HttpStatus.OK),
                new URI("https://maps.googleapis.com/maps/api/geocode/json?latlng=10.0,11.0&key=apiKey")
        );
        GoogleLocationProvider locationProvider = new MockGoogleLocationProvider(restTemplate);
        try {
            locationProvider.getLocation(10.0, 11.0);
            fail();
        } catch (LocationProviderException lpe) {
            assertEquals("Unable to retrieve location: Some error.", lpe.getMessage());
        }
    }

    private String readFile(String file) throws Exception {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(new DefaultResourceLoader().getResource(file).getInputStream(), "UTF-8");
        int i;
        while ((i = in.read(buffer, 0, buffer.length)) != -1) {
            out.append(buffer, 0, i);
        }
        return out.toString();
    }

    private ClientHttpResponse createResponse(String body, HttpStatus status) {
        MockClientHttpResponse response = new MockClientHttpResponse(body.getBytes(), status);
        response.getHeaders().setContentLength(body.length());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response;
    }

    private static class MockGoogleLocationProvider extends GoogleLocationProvider {

        private RestTemplate restTemplate;

        public MockGoogleLocationProvider(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
            apiKey = "apiKey";
        }

        @Override
        protected RestTemplate getRestTemplate() {
            return restTemplate;
        }
    }

    private static class MockRestTemplate extends RestTemplate {

        private ClientHttpResponse clientHttpResponse;
        private URI requiredUrl;

        public MockRestTemplate(ClientHttpResponse clientHttpResponse, URI requiredUrl) {
            super();
            this.clientHttpResponse = clientHttpResponse;
            this.requiredUrl = requiredUrl;
        }

        @Override
        protected <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
            return null;
        }

        @Override
        protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
            assertEquals(requiredUrl, url);
            MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, url);
            request.setResponse(clientHttpResponse);
            return request;
        }
    }

}