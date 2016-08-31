package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserMatchControllerTest {

    private UserMatchController userMatchController = new UserMatchController();
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private MatchResourceAssembler matchResourceAssembler = mock(MatchResourceAssembler.class);
    private User user;

    @Before
    public void setUp() throws Exception {
        userMatchController.setMatchRepository(matchRepository);
        userMatchController.setMatchResourceAssembler(matchResourceAssembler);
        user = new User();
        user.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetMutualMatches_invalidUser() throws Exception {
        ResponseEntity response = userMatchController.getMutualMatches(2L, mock(Pageable.class), mock(PagedResourcesAssembler.class), 1L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetMutualMatches() throws Exception {
        PagedResourcesAssembler pagedResourcesAssembler = mock(PagedResourcesAssembler.class);
        Pageable pageable = mock(Pageable.class);
        Date date = Date.from(Instant.ofEpochSecond(1472087710L));
        Page<Match> page = new PageImpl<>(new ArrayList<Match>());
        when(matchRepository.findMutualByUser(user, date, pageable)).thenReturn(page);
        userMatchController.getMutualMatches(1L, pageable, pagedResourcesAssembler, 1472087710L);

        verify(matchRepository, times(1)).findMutualByUser(user, date, pageable);
        verify(pagedResourcesAssembler, times(1)).toResource(any(Page.class), eq(matchResourceAssembler));
    }
}