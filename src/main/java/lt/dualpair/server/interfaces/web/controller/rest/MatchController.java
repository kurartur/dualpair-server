package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.MatchRequestException;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.dto.MatchDTO;
import lt.dualpair.server.interfaces.dto.assembler.MatchDTOAssembler;
import lt.dualpair.server.service.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MatchController {

    private MatchService matchService;
    private MatchDTOAssembler matchDTOAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/match/next")
    public ResponseEntity next(@RequestParam(name = "exclopp[]", required = false) List<Long> excludeOpponents) throws MatchRequestException {
        Match match;
        if (excludeOpponents != null) {
            match = matchService.nextFor(getUserPrincipal().getId(), excludeOpponents);
        } else {
            match = matchService.nextFor(getUserPrincipal().getId());
        }
        if (match == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(matchDTOAssembler.toDTO(match));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/match/{matchId:[0-9]+}")
    public ResponseEntity match(@PathVariable Long matchId) {
        Match match = matchService.getUserMatch(matchId, getUserPrincipal().getId());
        if (match == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(matchDTOAssembler.toDTO(match));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/match/{matchId:[0-9]+}/response")
    public ResponseEntity response(@PathVariable Long matchId, @RequestParam String response) throws URISyntaxException {
        matchService.responseByUser(matchId, MatchParty.Response.valueOf(response), getUserPrincipal().getId());
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI("/api/match/" + matchId)).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/matches")
    public ResponseEntity<Set<MatchDTO>> matches() {
        Set<Match> matches = matchService.getUserMutualMatches(getUserPrincipal().getId());
        return ResponseEntity.ok(matchDTOAssembler.toDTOSet(matches));
    }

    private User getUserPrincipal() {
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Autowired
    public void setMatchService(MatchService matchService) {
        this.matchService = matchService;
    }

    @Autowired
    public void setMatchDTOAssembler(MatchDTOAssembler matchDTOAssembler) {
        this.matchDTOAssembler = matchDTOAssembler;
    }
}
