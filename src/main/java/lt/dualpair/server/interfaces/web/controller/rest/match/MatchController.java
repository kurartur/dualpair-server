package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchRequestException;
import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.BaseController;
import lt.dualpair.server.service.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MatchController extends BaseController {

    private MatchService matchService;
    private MatchResourceAssembler matchResourceAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/match/next")
    public ResponseEntity next(@RequestParam(name = "exclopp[]", required = false) List<Long> excludeOpponents) throws MatchRequestException {
        Match match;
        match = matchService.nextFor(getUserPrincipal().getId(), excludeOpponents);
        if (match == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(matchResourceAssembler.toResource(new UserAwareMatch(getUserPrincipal(), match)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/match/{matchId:[0-9]+}")
    public ResponseEntity match(@PathVariable Long matchId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Autowired
    public void setMatchService(MatchService matchService) {
        this.matchService = matchService;
    }

    @Autowired
    public void setMatchResourceAssembler(MatchResourceAssembler matchResourceAssembler) {
        this.matchResourceAssembler = matchResourceAssembler;
    }
}
