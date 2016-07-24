package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.match.Match;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.MatchDTO;
import com.artur.dualpair.server.interfaces.dto.assembler.MatchDTOAssembler;
import com.artur.dualpair.server.service.match.MatchRequestException;
import com.artur.dualpair.server.service.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MatchController {

    private MatchService matchService;
    private MatchDTOAssembler matchDTOAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/match/next")
    public ResponseEntity next() throws MatchRequestException {
        Match match = matchService.nextFor(getUserPrincipal().getUserId());
        if (match == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(matchDTOAssembler.toDTO(match));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/match/{matchId:[0-9]+}")
    public ResponseEntity match(@PathVariable Long matchId) {
        Match match = matchService.getUserMatch(matchId, getUserPrincipal().getUsername());
        return ResponseEntity.ok(matchDTOAssembler.toDTO(match));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/match/{matchId:[0-9]+}/response")
    public ResponseEntity response(@PathVariable Long matchId, @RequestParam(required = true) String response) throws URISyntaxException {
        matchService.responseByUser(matchId, Match.Response.valueOf(response), getUserPrincipal().getUserId());
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI("/api/match/" + matchId)).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/matches")
    public ResponseEntity<Set<MatchDTO>> matches() {
        Set<Match> matches = matchService.getUserMatches(getUserPrincipal().getUsername());
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
