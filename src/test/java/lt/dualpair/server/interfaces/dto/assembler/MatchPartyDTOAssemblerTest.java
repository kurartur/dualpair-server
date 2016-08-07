package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.dto.MatchPartyDTO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class MatchPartyDTOAssemblerTest {

    private MatchPartyDTOAssembler matchPartyDTOAssembler = new MatchPartyDTOAssembler();
    private UserDTOAssembler userDTOAssembler = mock(UserDTOAssembler.class);

    @Before
    public void setUp() throws Exception {
        matchPartyDTOAssembler.setUserDTOAssembler(userDTOAssembler);
    }

    @Test
    public void testToDTO() throws Exception {
        User user = new User();
        MatchParty matchParty = new MatchParty();
        matchParty.setUser(user);
        matchParty.setResponse(MatchParty.Response.YES);
        MatchPartyDTO dto = matchPartyDTOAssembler.toDTO(matchParty);
        assertEquals("YES", dto.getResponse());
        verify(userDTOAssembler, times(1)).toDTO(user);
    }
}