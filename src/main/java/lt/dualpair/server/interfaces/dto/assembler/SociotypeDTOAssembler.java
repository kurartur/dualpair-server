package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.interfaces.dto.SociotypeDTO;
import org.springframework.stereotype.Component;

@Component
public class SociotypeDTOAssembler extends DTOAssembler<Sociotype, SociotypeDTO> {

    @Override
    public Sociotype toEntity(SociotypeDTO sociotypeDTO) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public SociotypeDTO toDTO(Sociotype sociotype) {
        SociotypeDTO dto = new SociotypeDTO();
        if (sociotype.getCode1() != null) {
            dto.setCode1(sociotype.getCode1().name());
        }
        if (sociotype.getCode2() != null) {
            dto.setCode2(sociotype.getCode2().name());
        }
        return dto;
    }

}
