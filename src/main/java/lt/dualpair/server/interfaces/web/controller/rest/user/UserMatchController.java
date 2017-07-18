package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.UserAwareMatch;
import lt.dualpair.core.user.MatchRepository;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.security.UserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/{userId:[0-9]+}")
public class UserMatchController {

    private MatchRepository matchRepository;
    private MatchResourceAssembler matchResourceAssembler;
    private UserRepository userRepository;

    @Inject
    public UserMatchController(MatchRepository matchRepository, MatchResourceAssembler matchResourceAssembler, UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.matchResourceAssembler = matchResourceAssembler;
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/matches/{matchId:[0-9]+}")
    public ResponseEntity getMatch(@PathVariable Long userId, @PathVariable Long matchId, @ActiveUser UserDetails user) {
        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Match> matchOpt = matchRepository.findOneByUser(userId, matchId);
        if (!matchOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Match match = matchOpt.get();
        return ResponseEntity.ok(matchResourceAssembler.toResource(new UserAwareMatch(match.getMatchParty(user.getId()).getUser(), match)));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/matches", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getMatches(@PathVariable Long userId,
                                     Pageable pageable,
                                     PagedResourcesAssembler pagedResourcesAssembler,
                                     @RequestParam Long timestamp,
                                     @ActiveUser UserDetails principal,
                                     @RequestParam("mt") MatchType matchType) {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<Match> matches = getMatchesByType(userRepository.findById(userId).get(), matchType, timestamp, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(matches.map(source -> new UserAwareMatch(userId, source)), matchResourceAssembler));
    }

    private Page<Match> getMatchesByType(User user, MatchType matchType, Long timestamp, Pageable pageable) {
        Date date = Date.from(Instant.ofEpochSecond(timestamp));
        if (matchType == MatchType.mu) {
            return matchRepository.findMutual(user, date, pageable);
        } else {
            return matchRepository.findReviewed(user, date, pageable);
        }
    }

    enum MatchType {
        mu, re;
    }
}
