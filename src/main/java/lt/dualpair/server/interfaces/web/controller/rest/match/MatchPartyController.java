package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.match.Response;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import lt.dualpair.server.infrastructure.persistence.repository.MatchPartyRepository;
import lt.dualpair.server.service.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/party")
public class MatchPartyController {

    private MatchService matchService;
    private MatchPartyRepository matchPartyRepository;

    @RequestMapping(method = RequestMethod.PUT, value = "/{partyId:[0-9]+}/response")
    public ResponseEntity response(@PathVariable Long partyId, @RequestBody String responseString, @ActiveUser User principal) {
        Response response = Response.valueOf(responseString); // TODO make Response as method's type
        Optional<MatchParty> optMatchParty = matchPartyRepository.findById(partyId);
        if (!optMatchParty.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        MatchParty matchParty = optMatchParty.get();
        if (!principal.getId().equals(matchParty.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        matchParty.setResponse(response);
        matchPartyRepository.save(matchParty);
        Match match = matchParty.getMatch();
        matchService.sendMutualMatchNotifications(match);
        return ResponseEntity.ok().build();
    }

    @Autowired
    public void setMatchService(MatchService matchService) {
        this.matchService = matchService;
    }

    @Autowired
    public void setMatchPartyRepository(MatchPartyRepository matchPartyRepository) {
        this.matchPartyRepository = matchPartyRepository;
    }
}
