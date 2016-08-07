package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.interfaces.dto.MatchDTO;
import lt.dualpair.server.interfaces.dto.MatchPartyDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static lt.dualpair.server.domain.model.match.MatchPartyTestUtils.createMatchParty;
import static lt.dualpair.server.domain.model.user.UserTestUtils.createUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchDTOAssemblerTest {

    private MatchDTOAssembler matchDTOAssembler = new MatchDTOAssembler();
    private MatchPartyDTOAssembler matchPartyDTOAssembler = mock(MatchPartyDTOAssembler.class);

    @Before
    public void setUp() throws Exception {
        matchDTOAssembler.setMatchPartyDTOAssembler(matchPartyDTOAssembler);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(createUser(2L), ""));
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testToDTO() throws Exception {
        Match match = new Match();
        match.setId(1L);
        match.setDistance(10);
        MatchParty party1 = createMatchParty(1L, createUser(1L), MatchParty.Response.NO);
        MatchPartyDTO partyDTO1 = new MatchPartyDTO();
        MatchParty party2 = createMatchParty(2L, createUser(2L), MatchParty.Response.YES);
        MatchPartyDTO partyDTO2 = new MatchPartyDTO();
        when(matchPartyDTOAssembler.toDTO(party1)).thenReturn(partyDTO1);
        when(matchPartyDTOAssembler.toDTO(party2)).thenReturn(partyDTO2);
        match.setMatchParties(party1, party2);
        MatchDTO matchDTO = matchDTOAssembler.toDTO(match);
        assertEquals((Long)1L, matchDTO.getId());
        assertEquals(partyDTO1, matchDTO.getOpponent());
        assertEquals(partyDTO2, matchDTO.getUser());
        assertEquals(10, (int)matchDTO.getDistance());
    }

}