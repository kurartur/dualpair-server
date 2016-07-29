package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.dto.MatchDTO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MatchDTOAssemblerTest {

    private MatchDTOAssembler matchDTOAssembler;
    private UserDTOAssembler userDTOAssembler = mock(UserDTOAssembler.class);

    @Before
    public void setUp() throws Exception {
        matchDTOAssembler = new MatchDTOAssembler(userDTOAssembler);
    }

    @Test
    public void testToDTO() throws Exception {
        Match match = new Match();
        User user = createUser("user");
        User opponent = createUser("opponent");
        match.setUser(user);
        match.setOpponent(opponent);
        match.setResponse(Match.Response.YES);
        MatchDTO matchDTO = matchDTOAssembler.toDTO(match);
        assertEquals("YES", matchDTO.getResponse());
        verify(userDTOAssembler, times(1)).toDTO(user);
        verify(userDTOAssembler, times(1)).toDTO(opponent);
    }

    private User createUser(String name) {
        User user = new User();
        user.setName(name);
        return user;
    }
}