package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.match.Match;
import com.artur.dualpair.server.interfaces.dto.MatchDTO;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class MatchDTOAssembler extends DTOAssembler<Match, MatchDTO> {

    private UserDTOAssembler userDTOAssembler;

    @Inject
    public MatchDTOAssembler(UserDTOAssembler userDTOAssembler) {
        this.userDTOAssembler = userDTOAssembler;
    }

    @Override
    public Match toEntity(MatchDTO matchDTO) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MatchDTO toDTO(Match match) {
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setUser(userDTOAssembler.toDTO(match.getUser()));
        matchDTO.setOpponent(userDTOAssembler.toDTO(match.getOpponent()));
        matchDTO.setResponse(match.getResponse().name());
        return matchDTO;
    }
}
