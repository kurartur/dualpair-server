package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.geo.Location;
import com.artur.dualpair.server.interfaces.dto.LocationDTO;

public class LocationDTOAssembler extends DTOAssembler<Location, LocationDTO> {

    @Override
    public LocationDTO toDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setCountryCode(location.getCountryCode());
        dto.setCity(location.getCity());
        return dto;
    }

    @Override
    public Location toEntity(LocationDTO dto) {
        return new Location(
                dto.getLatitude(),
                dto.getLongitude(),
                dto.getCountryCode(),
                dto.getCity()
        );
    }
}
