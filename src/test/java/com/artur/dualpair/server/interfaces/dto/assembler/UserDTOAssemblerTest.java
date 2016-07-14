package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UserDTOAssemblerTest {

    private UserDTOAssembler userDTOAssembler;

    @Before
    public void setUp() throws Exception {
        userDTOAssembler = new UserDTOAssembler(new SociotypeDTOAssembler());
    }

    @Test
    public void testToDTO() throws Exception {
        User user = new User();
        user.setName("name");
        Date birthday = new Date();
        user.setDateOfBirth(birthday);
        user.setSociotypes(createSociotypes(Sociotype.Code1.EII));
        UserDTO userDTO = userDTOAssembler.toDTO(user);
        assertEquals("name", userDTO.getName());
        assertEquals((Integer)0, userDTO.getAge());
        assertEquals("EII", userDTO.getSociotypes().iterator().next().getCode1());
        assertEquals(birthday, userDTO.getDateOfBirth());
    }

    private Set<Sociotype> createSociotypes(Sociotype.Code1 code1) {
        Set<Sociotype> sociotypes = new HashSet<>();
        sociotypes.add(new Sociotype.Builder().code1(code1).build());
        return sociotypes;
    }
}