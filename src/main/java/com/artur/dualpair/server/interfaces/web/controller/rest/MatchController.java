package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.assembler.MatchDTOAssembler;
import com.artur.dualpair.server.service.match.MatchRequestException;
import com.artur.dualpair.server.service.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MatchController {

    private MatchService matchService;
    private MatchDTOAssembler matchDTOAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/match/next")
    public ResponseEntity next() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Match match = matchService.nextFor(user.getUserId());

            if (match == null) {
                return ResponseEntity.ok().body("No matches");
            }
            return ResponseEntity.ok().body(matchDTOAssembler.toDTO(match));
        } catch (MatchRequestException mre) {
            return ResponseEntity.badRequest().body(mre.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/match/{matchId:[0-9]+}")
    public ResponseEntity match(@PathVariable Long matchId) {
        try {
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Match match = matchService.getUserMatch(matchId, user.getUsername());
            return ResponseEntity.ok(matchDTOAssembler.toDTO(match));
        } catch (Exception e) {
            if ("Invalid user".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.from(e));
            }
            return ResponseEntity.badRequest().body(ErrorResponse.from(e));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/match/{matchId:[0-9]+}/response")
    public ResponseEntity response(@PathVariable Long matchId, @RequestParam(required = true) String response) throws URISyntaxException {
        try {
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            matchService.responseByUser(matchId, Match.Response.valueOf(response), user.getUserId());
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI("/api/match/" + matchId)).build();
        } catch (Exception iae) {
            if ("Invalid user".equals(iae.getMessage()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.from(iae));
            return ResponseEntity.badRequest().body(ErrorResponse.from(iae));
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/matches")
    public ResponseEntity matches() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Match> matches = matchService.getUserMatches(user.getUsername());
        return ResponseEntity.ok(matchDTOAssembler.toDTOSet(matches));
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
