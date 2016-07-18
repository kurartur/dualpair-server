package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.geo.Location;
import com.artur.dualpair.server.interfaces.dto.LocationDTO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationDTOAssemblerTest {

    private LocationDTOAssembler assembler = new LocationDTOAssembler();

    @Test
    public void testToDTO() throws Exception {
        Location location = new Location(1.0, 2.0, "LT", "Vilnius");
        LocationDTO dto = assembler.toDTO(location);
        assertEquals(new Double(1.0), dto.getLatitude());
        assertEquals(new Double(2.0), dto.getLongitude());
        assertEquals("LT", dto.getCountryCode());
        assertEquals("Vilnius", dto.getCity());
    }

    @Test
    public void testToEntity() throws Exception {
        LocationDTO dto = new LocationDTO();
        dto.setLatitude(1.0);
        dto.setLongitude(2.0);
        dto.setCountryCode("LT");
        dto.setCity("Vilnius");
        Location location = assembler.toEntity(dto);
        assertEquals(new Double(1.0), location.getLatitude());
        assertEquals(new Double(2.0), location.getLongitude());
        assertEquals("LT", location.getCountryCode());
        assertEquals("Vilnius", location.getCity());
    }
}