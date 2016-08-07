package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.interfaces.dto.MatchPartyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchPartyDTOAssembler extends DTOAssembler<MatchParty, MatchPartyDTO> {

    private UserDTOAssembler userDTOAssembler;

    @Override
    public MatchParty toEntity(MatchPartyDTO matchPartyDTO) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MatchPartyDTO toDTO(MatchParty matchParty) {
        MatchPartyDTO dto = new MatchPartyDTO();
        dto.setResponse(matchParty.getResponse().name());
        dto.setUser(userDTOAssembler.toDTO(matchParty.getUser()));
        return dto;
    }

    @Autowired
    public void setUserDTOAssembler(UserDTOAssembler userDTOAssembler) {
        this.userDTOAssembler = userDTOAssembler;
    }
}
