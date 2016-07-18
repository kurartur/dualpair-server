package com.artur.dualpair.server.domain.model.geo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultipleServiceLocationProviderTest {

    private MultipleServiceLocationProvider multipleServiceLocationProvider = new MultipleServiceLocationProvider();
    private GoogleLocationProvider googleLocationProvider = mock(GoogleLocationProvider.class);

    @Before
    public void setUp() throws Exception {
        multipleServiceLocationProvider.setGoogleLocationProvider(googleLocationProvider);
    }

    @Test
    public void testGetLocation_google() throws Exception {
        Location location = new Location(1.0, 2.0, "LT", "Vilnius");
        when(googleLocationProvider.getLocation(1.0, 2.0)).thenReturn(location);
        assertEquals(location, multipleServiceLocationProvider.getLocation(1.0, 2.0));
    }
}