package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.dto.MatchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MatchDTOAssembler extends DTOAssembler<Match, MatchDTO> {

    private MatchPartyDTOAssembler matchPartyDTOAssembler;

    @Override
    public Match toEntity(MatchDTO matchDTO) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MatchDTO toDTO(Match match) {
        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setId(match.getId());
        matchDTO.setUser(matchPartyDTOAssembler.toDTO(match.getMatchParty(currentUser.getId())));
        matchDTO.setOpponent(matchPartyDTOAssembler.toDTO(match.getOppositeMatchParty(currentUser.getId())));
        matchDTO.setDistance(match.getDistance());
        return matchDTO;
    }

    @Autowired
    public void setMatchPartyDTOAssembler(MatchPartyDTOAssembler matchPartyDTOAssembler) {
        this.matchPartyDTOAssembler = matchPartyDTOAssembler;
    }
}
