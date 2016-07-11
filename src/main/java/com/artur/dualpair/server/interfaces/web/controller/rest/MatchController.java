package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.interfaces.dto.assembler.MatchDTOAssembler;
import com.artur.dualpair.server.service.match.MatchRequestException;
import com.artur.dualpair.server.service.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.POST, value = "/match/{matchId:[0-9]+}/response")
    public void response(@PathVariable Long matchId, @RequestParam String response) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            matchService.responseByUser(matchId, Match.Response.valueOf(response), user.getUserId());
        } catch (IllegalArgumentException iae) {
            if ("Invalid user".equals(iae.getMessage()))
                throw new InsufficientPrivilegesException(InsufficientPrivilegesException.illegalAccess, iae);
            throw iae;
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity list() {
        return null;
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
