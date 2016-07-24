package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.geo.Location;
import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.photo.Photo;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserDTOAssemblerTest {

    private UserDTOAssembler userDTOAssembler = new UserDTOAssembler();;
    private LocationDTOAssembler locationDTOAssembler = mock(LocationDTOAssembler.class);
    private PhotoDTOAssembler photoDTOAssembler = mock(PhotoDTOAssembler.class);
    private SearchParametersDTOAssembler searchParametersDTOAssembler = mock(SearchParametersDTOAssembler.class);
    private SociotypeDTOAssembler sociotypeDTOAssembler = mock(SociotypeDTOAssembler.class);

    @Before
    public void setUp() throws Exception {
        userDTOAssembler.setLocationDTOAssembler(locationDTOAssembler);
        userDTOAssembler.setPhotoDTOAssembler(photoDTOAssembler);
        userDTOAssembler.setSearchParametersDTOAssembler(searchParametersDTOAssembler);
        userDTOAssembler.setSociotypeDTOAssembler(sociotypeDTOAssembler);
    }

    @Test
    public void testToDTO() throws Exception {
        User user = new User();
        user.setName("name");
        Date birthday = new Date();
        user.setDateOfBirth(birthday);
        Set<Sociotype> sociotypes = new HashSet<>(Arrays.asList(new Sociotype.Builder().build()));
        user.setSociotypes(sociotypes);
        SociotypeDTO sociotypeDTO = new SociotypeDTO();
        doReturn(new HashSet<>(Arrays.asList(sociotypeDTO))).when(sociotypeDTOAssembler).toDTOSet(sociotypes);
        user.setDescription("description");
        SearchParameters searchParameters = new SearchParameters();
        SearchParametersDTO searchParametersDTO = new SearchParametersDTO();
        Location location = new Location(10.0, 10.0, "LT", "Vilnius");
        LocationDTO locationDTO = new LocationDTO();
        searchParameters.setLocation(location);
        user.setSearchParameters(searchParameters);
        doReturn(searchParametersDTO).when(searchParametersDTOAssembler).toDTO(searchParameters);
        when(locationDTOAssembler.toDTO(location)).thenReturn(locationDTO);
        Photo photo = new Photo();
        user.setPhotos(Arrays.asList(photo));
        PhotoDTO photoDTO = new PhotoDTO();
        doReturn(Arrays.asList(photoDTO)).when(photoDTOAssembler).toDTOList(user.getPhotos());
        UserDTO userDTO = userDTOAssembler.toDTO(user);
        assertEquals("name", userDTO.getName());
        assertEquals((Integer)0, userDTO.getAge());
        assertEquals(sociotypeDTO, userDTO.getSociotypes().iterator().next());
        assertEquals(birthday, userDTO.getDateOfBirth());
        assertEquals(searchParametersDTO, userDTO.getSearchParameters());
        assertEquals(locationDTO, userDTO.getLocation());
        assertEquals("description", userDTO.getDescription());
        assertEquals(photoDTO, userDTO.getPhotos().iterator().next());
    }
}