package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.interfaces.web.controller.rest.match.MatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class MatchResourceAssembler extends ResourceAssemblerSupport<UserAwareMatch, MatchResource> {

    private UserMatchPartyResourceAssembler userMatchPartyResourceAssembler;
    private OpponentMatchPartyResourceAssembler opponentMatchPartyResourceAssembler;

    public MatchResourceAssembler() {
        super(MatchController.class, MatchResource.class);
    }

    @Override
    public MatchResource toResource(UserAwareMatch entity) {
        MatchResource resource = new MatchResource();
        resource.setMatchId(entity.getId());
        resource.setUser(userMatchPartyResourceAssembler.toResource(entity.getUserMatchParty()));
        resource.setOpponent(opponentMatchPartyResourceAssembler.toResource(entity.getOpponentMatchParty()));
        resource.setDistance(entity.getDistance());
        return resource;
    }

    @Autowired
    public void setUserMatchPartyResourceAssembler(UserMatchPartyResourceAssembler userMatchPartyResourceAssembler) {
        this.userMatchPartyResourceAssembler = userMatchPartyResourceAssembler;
    }

    @Autowired
    public void setOpponentMatchPartyResourceAssembler(OpponentMatchPartyResourceAssembler opponentMatchPartyResourceAssembler) {
        this.opponentMatchPartyResourceAssembler = opponentMatchPartyResourceAssembler;
    }
}
