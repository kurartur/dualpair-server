package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserMatchControllerTest {

    private UserMatchController userMatchController = new UserMatchController();
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private MatchResourceAssembler matchResourceAssembler = mock(MatchResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        userMatchController.setMatchRepository(matchRepository);
        userMatchController.setMatchResourceAssembler(matchResourceAssembler);
    }

    @Test
    public void testGetMutualMatches_invalidUser() throws Exception {
        ResponseEntity response = userMatchController.getMutualMatches(2L,
                mock(Pageable.class),
                mock(PagedResourcesAssembler.class),
                1L,
                UserTestUtils.createUser(1L));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetMutualMatches() throws Exception {
        PagedResourcesAssembler pagedResourcesAssembler = mock(PagedResourcesAssembler.class);
        Pageable pageable = mock(Pageable.class);
        Date date = Date.from(Instant.ofEpochSecond(1472087710L));
        Page<Match> page = new PageImpl<>(new ArrayList<Match>());
        when(matchRepository.findMutualByUser(1L, date, pageable)).thenReturn(page);
        userMatchController.getMutualMatches(1L, pageable, pagedResourcesAssembler, 1472087710L, UserTestUtils.createUser(1L));

        verify(matchRepository, times(1)).findMutualByUser(1L, date, pageable);
        verify(pagedResourcesAssembler, times(1)).toResource(any(Page.class), eq(matchResourceAssembler));
    }
}