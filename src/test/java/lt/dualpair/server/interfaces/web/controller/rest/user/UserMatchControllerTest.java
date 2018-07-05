package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchTestUtils;
import lt.dualpair.core.match.UserAwareMatch;
import lt.dualpair.core.user.MatchRepository;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.core.user.UserTestUtils;
import lt.dualpair.server.interfaces.resource.match.MatchResource;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.security.TestUserDetails;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserMatchControllerTest {

    private UserMatchController userMatchController;
    private MatchRepository matchRepository = mock(MatchRepository.class);
    private MatchResourceAssembler matchResourceAssembler = mock(MatchResourceAssembler.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private User user = UserTestUtils.createUser();

    @Before
    public void setUp() throws Exception {
        userMatchController = new UserMatchController(matchRepository, matchResourceAssembler, userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    public void testGetMatch_invalidUser() throws Exception {
        ResponseEntity response = userMatchController.getMatch(2L, 1L, new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetMatch_notFound() throws Exception {
        when(matchRepository.findOneByUser(1L, 1L)).thenReturn(Optional.empty());
        ResponseEntity response = userMatchController.getMatch(1L, 1L, new TestUserDetails(1L));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetMatch() throws Exception {
        Match match = MatchTestUtils.createMatch();
        MatchResource matchResource = new MatchResource();
        when(matchRepository.findOneByUser(1L, 1L)).thenReturn(Optional.of(match));
        when(matchResourceAssembler.toResource(any(UserAwareMatch.class))).thenReturn(matchResource);
        ResponseEntity response = userMatchController.getMatch(1L, 1L, new TestUserDetails(1L));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ArgumentCaptor<UserAwareMatch> captor = ArgumentCaptor.forClass(UserAwareMatch.class);
        verify(matchResourceAssembler, times(1)).toResource(captor.capture());
        assertEquals(match.getId(), captor.getValue().getId());
        assertEquals(matchResource, response.getBody());
    }

    @Test
    public void testGetMatches_invalidUser() throws Exception {
        ResponseEntity response = userMatchController.getMatches(2L,
                mock(Pageable.class),
                mock(PagedResourcesAssembler.class),
                1L,
                new TestUserDetails(1L));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetMatches_mutual() throws Exception {
        PagedResourcesAssembler pagedResourcesAssembler = mock(PagedResourcesAssembler.class);
        Pageable pageable = mock(Pageable.class);
        Date date = Date.from(Instant.ofEpochSecond(1472087710L));
        Page<Match> page = new PageImpl<>(new ArrayList<Match>());
        when(matchRepository.fetchMatches(1L, date, pageable)).thenReturn(page);
        userMatchController.getMatches(1L, pageable, pagedResourcesAssembler, 1472087710L, new TestUserDetails(1L));

        verify(matchRepository, times(1)).fetchMatches(1L, date, pageable);
        verify(pagedResourcesAssembler, times(1)).toResource(any(Page.class), eq(matchResourceAssembler));
    }

}