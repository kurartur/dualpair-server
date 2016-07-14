package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.UserDTO;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class UserDTOAssembler extends DTOAssembler<User, UserDTO> {

    private SociotypeDTOAssembler sociotypeDTOAssembler;

    @Inject
    public UserDTOAssembler(SociotypeDTOAssembler sociotypeDTOAssembler) {
        this.sociotypeDTOAssembler = sociotypeDTOAssembler;
    }

    @Override
    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setAge(user.getAge());
        userDTO.setSociotypes(sociotypeDTOAssembler.toDTOSet(user.getSociotypes()));
        return userDTO;
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
