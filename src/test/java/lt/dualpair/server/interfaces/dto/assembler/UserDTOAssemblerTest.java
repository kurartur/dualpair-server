package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;
import lt.dualpair.server.interfaces.dto.PhotoDTO;
import lt.dualpair.server.interfaces.dto.SearchParametersDTO;
import lt.dualpair.server.interfaces.dto.SociotypeDTO;
import lt.dualpair.server.interfaces.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UserDTOAssemblerTest {

    private UserDTOAssembler userDTOAssembler = new UserDTOAssembler();;
    private PhotoDTOAssembler photoDTOAssembler = mock(PhotoDTOAssembler.class);
    private SearchParametersDTOAssembler searchParametersDTOAssembler = mock(SearchParametersDTOAssembler.class);
    private SociotypeDTOAssembler sociotypeDTOAssembler = mock(SociotypeDTOAssembler.class);

    @Before
    public void setUp() throws Exception {
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
        UserLocation location = new UserLocation(user, 10.0, 11.0, "LT", "Vilnius");
        user.addLocation(location);
        user.setSearchParameters(searchParameters);
        doReturn(searchParametersDTO).when(searchParametersDTOAssembler).toDTO(searchParameters);
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
        assertEquals("LT", userDTO.getLocation().getCountryCode());
        assertEquals((Double)10.0, userDTO.getLocation().getLatitude());
        assertEquals((Double)11.0, userDTO.getLocation().getLongitude());
        assertEquals("Vilnius", userDTO.getLocation().getCity());
        assertEquals("description", userDTO.getDescription());
        assertEquals(photoDTO, userDTO.getPhotos().iterator().next());
    }
}