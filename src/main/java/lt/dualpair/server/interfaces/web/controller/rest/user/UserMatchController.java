package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.UserAwareMatch;
import lt.dualpair.core.user.MatchRepository;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.security.UserDetails;
import lt.dualpair.server.service.user.UserMatchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RestController
@PreAuthorize("@authorizer.hasPermission(authentication, #userId)")
@RequestMapping("/api/user/{userId:[0-9]+}")
public class UserMatchController {

    private MatchRepository matchRepository;
    private MatchResourceAssembler matchResourceAssembler;
    private UserMatchService userMatchService;

    @Inject
    public UserMatchController(MatchRepository matchRepository, MatchResourceAssembler matchResourceAssembler, UserMatchService userMatchService) {
        this.matchRepository = matchRepository;
        this.matchResourceAssembler = matchResourceAssembler;
        this.userMatchService = userMatchService;
    }

    @GetMapping("/matches/{matchId:[0-9]+}")
    public ResponseEntity getMatch(@PathVariable Long userId, @PathVariable Long matchId, @ActiveUser UserDetails user) {
        Optional<Match> matchOpt = matchRepository.findOneByUser(userId, matchId);
        if (!matchOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Match match = matchOpt.get();
        return ResponseEntity.ok(matchResourceAssembler.toResource(new UserAwareMatch(match.getMatchParty(user.getId()).getUser(), match)));
    }

    @DeleteMapping(path = "/matches/{matchId:[0-9]+}")
    public ResponseEntity unmatch(@PathVariable Long userId, @PathVariable Long matchId, @ActiveUser UserDetails user) {
        userMatchService.unmatch(matchId, userId);
        return ResponseEntity.ok().build();
    }


    @GetMapping(path = "/matches", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getMatches(@PathVariable Long userId,
                                     Pageable pageable,
                                     PagedResourcesAssembler pagedResourcesAssembler,
                                     @RequestParam Long timestamp,
                                     @ActiveUser UserDetails principal) {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Date date = Date.from(Instant.ofEpochSecond(timestamp));
        Page<Match> matches = matchRepository.fetchMatches(userId, date, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(matches.map(source -> new UserAwareMatch(userId, source)), matchResourceAssembler));
    }
}
