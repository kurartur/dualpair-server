package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.MatchDTO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatchDTOAssemblerTest {

    private MatchDTOAssembler matchDTOAssembler;

    @Before
    public void setUp() throws Exception {
        matchDTOAssembler = new MatchDTOAssembler(new UserDTOAssembler(new SociotypeDTOAssembler(), new SearchParametersDTOAssembler()));
    }

    @Test
    public void testToDTO() throws Exception {
        Match match = new Match();
        match.setUser(createUser("user"));
        match.setOpponent(createUser("opponent"));
        match.setResponse(Match.Response.YES);
        MatchDTO matchDTO = matchDTOAssembler.toDTO(match);
        assertEquals("user", matchDTO.getUser().getName());
        assertEquals("opponent", matchDTO.getOpponent().getName());
        assertEquals("YES", matchDTO.getResponse());
    }

    private User createUser(String name) {
        User user = new User();
        user.setName(name);
        return user;
    }
}