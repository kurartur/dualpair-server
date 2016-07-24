package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.geo.Location;
import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.photo.Photo;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.LocationDTO;
import com.artur.dualpair.server.interfaces.dto.PhotoDTO;
import com.artur.dualpair.server.interfaces.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserDTOAssemblerTest {

    private UserDTOAssembler userDTOAssembler;
    private LocationDTOAssembler locationDTOAssembler = mock(LocationDTOAssembler.class);
    private PhotoDTOAssembler photoDTOAssembler = mock(PhotoDTOAssembler.class);

    @Before
    public void setUp() throws Exception {
        userDTOAssembler = new UserDTOAssembler();
        userDTOAssembler.setLocationDTOAssembler(locationDTOAssembler);
        userDTOAssembler.setPhotoDTOAssembler(photoDTOAssembler);

        // TODO make these as mocks
        userDTOAssembler.setSearchParametersDTOAssembler(new SearchParametersDTOAssembler());
        userDTOAssembler.setSociotypeDTOAssembler(new SociotypeDTOAssembler());
    }

    @Test
    public void testToDTO() throws Exception {
        User user = new User();
        user.setName("name");
        Date birthday = new Date();
        user.setDateOfBirth(birthday);
        user.setSociotypes(createSociotypes(Sociotype.Code1.EII));
        user.setDescription("description");
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(20);
        Location location = new Location(10.0, 10.0, "LT", "Vilnius");
        LocationDTO locationDTO = new LocationDTO();
        searchParameters.setLocation(location);
        user.setSearchParameters(searchParameters);
        when(locationDTOAssembler.toDTO(location)).thenReturn(locationDTO);
        Photo photo = new Photo();
        user.setPhotos(Arrays.asList(photo));
        PhotoDTO photoDTO = new PhotoDTO();
        doReturn(Arrays.asList(photoDTO)).when(photoDTOAssembler).toDTOList(user.getPhotos());
        UserDTO userDTO = userDTOAssembler.toDTO(user);
        assertEquals("name", userDTO.getName());
        assertEquals((Integer)0, userDTO.getAge());
        assertEquals("EII", userDTO.getSociotypes().iterator().next().getCode1());
        assertEquals(birthday, userDTO.getDateOfBirth());
        assertEquals((Integer)20, userDTO.getSearchParameters().getMinAge());
        assertEquals(locationDTO, userDTO.getLocation());
        assertEquals("description", userDTO.getDescription());
        assertEquals(photoDTO, userDTO.getPhotos().iterator().next());
    }

    private Set<Sociotype> createSociotypes(Sociotype.Code1 code1) {
        Set<Sociotype> sociotypes = new HashSet<>();
        sociotypes.add(new Sociotype.Builder().code1(code1).build());
        return sociotypes;
    }
}