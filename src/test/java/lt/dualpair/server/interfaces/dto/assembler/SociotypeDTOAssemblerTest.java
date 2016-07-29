package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.interfaces.dto.SociotypeDTO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SociotypeDTOAssemblerTest {

    private SociotypeDTOAssembler sociotypeDTOAssembler = new SociotypeDTOAssembler();

    @Test
    public void testToDTO() throws Exception {
        Sociotype sociotype = new Sociotype.Builder().code1(Sociotype.Code1.EII).code2(Sociotype.Code2.ENTJ).build();
        SociotypeDTO dto = sociotypeDTOAssembler.toDTO(sociotype);
        assertEquals("EII", dto.getCode1());
        assertEquals("ENTJ", dto.getCode2());
    }

    @Test
    public void testToDTO_nullValues() throws Exception {
        Sociotype sociotype = new Sociotype.Builder().code1(null).code2(Sociotype.Code2.ENTJ).build();
        SociotypeDTO dto = sociotypeDTOAssembler.toDTO(sociotype);
        assertNull(dto.getCode1());
        assertEquals("ENTJ", dto.getCode2());

        sociotype = new Sociotype.Builder().code1(Sociotype.Code1.EII).code2(null).build();
        dto = sociotypeDTOAssembler.toDTO(sociotype);
        assertEquals("EII", dto.getCode1());
        assertNull(dto.getCode2());
    }
}