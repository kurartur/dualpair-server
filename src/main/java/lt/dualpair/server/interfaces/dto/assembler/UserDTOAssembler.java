package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;
import lt.dualpair.server.interfaces.dto.LocationDTO;
import lt.dualpair.server.interfaces.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOAssembler extends DTOAssembler<User, UserDTO> {

    private SociotypeDTOAssembler sociotypeDTOAssembler;

    private SearchParametersDTOAssembler searchParametersDTOAssembler;

    private PhotoDTOAssembler photoDTOAssembler;

    @Override
    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setAge(user.getAge());
        userDTO.setSociotypes(sociotypeDTOAssembler.toDTOSet(user.getSociotypes()));
        userDTO.setDescription(user.getDescription());
        if (user.getSearchParameters() != null) {
            userDTO.setSearchParameters(searchParametersDTOAssembler.toDTO(user.getSearchParameters()));
            UserLocation userLocation = user.getRecentLocation();
            if (userLocation != null) {
                LocationDTO locationDTO = new LocationDTO();
                locationDTO.setLatitude(userLocation.getLatitude());
                locationDTO.setLongitude(userLocation.getLongitude());
                locationDTO.setCity(userLocation.getCity());
                locationDTO.setCountryCode(userLocation.getCountryCode());
                userDTO.setLocation(locationDTO);
            }
        }
        if (user.getPhotos() != null) {
            userDTO.setPhotos(photoDTOAssembler.toDTOList(user.getPhotos()));
        }
        return userDTO;
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Autowired
    public void setSociotypeDTOAssembler(SociotypeDTOAssembler sociotypeDTOAssembler) {
        this.sociotypeDTOAssembler = sociotypeDTOAssembler;
    }

    @Autowired
    public void setSearchParametersDTOAssembler(SearchParametersDTOAssembler searchParametersDTOAssembler) {
        this.searchParametersDTOAssembler = searchParametersDTOAssembler;
    }

    @Autowired
    public void setPhotoDTOAssembler(PhotoDTOAssembler photoDTOAssembler) {
        this.photoDTOAssembler = photoDTOAssembler;
    }
}
