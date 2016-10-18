package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/{userId:[0-9]+}")
public class UserMatchController {

    private MatchRepository matchRepository;
    private MatchResourceAssembler matchResourceAssembler;

    @RequestMapping(method = RequestMethod.GET, path = "/matches/{matchId:[0-9]+}")
    public ResponseEntity getMatch(@PathVariable Long userId, @PathVariable Long matchId, @ActiveUser User user) {
        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Match> match = matchRepository.findOneByUser(userId, matchId);
        if (!match.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(matchResourceAssembler.toResource(new UserAwareMatch(user, match.get())));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/matches", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getMatches(@PathVariable Long userId,
                                     Pageable pageable,
                                     PagedResourcesAssembler pagedResourcesAssembler,
                                     @RequestParam Long timestamp,
                                     @ActiveUser User principal,
                                     @RequestParam("mt") MatchType matchType) {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<Match> matches = getMatchesByType(principal, matchType, timestamp, pageable);
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

    @Autowired
    public void setMatchRepository(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Autowired
    public void setMatchResourceAssembler(MatchResourceAssembler matchResourceAssembler) {
        this.matchResourceAssembler = matchResourceAssembler;
    }

    enum MatchType {
        mu, re;
    }
}
