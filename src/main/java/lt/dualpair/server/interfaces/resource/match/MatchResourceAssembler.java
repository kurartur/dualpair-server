package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.interfaces.web.controller.rest.MatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class MatchResourceAssembler extends ResourceAssemblerSupport<UserAwareMatch, MatchResource> {

    private BasicMatchPartyResourceAssembler basicMatchPartyResourceAssembler;
    private FullMatchPartyResourceAssembler fullMatchPartyResourceAssembler;

    public MatchResourceAssembler() {
        super(MatchController.class, MatchResource.class);
    }

    @Override
    public MatchResource toResource(UserAwareMatch entity) {
        MatchResource resource = new MatchResource();
        resource.setMatchId(entity.getId());
        resource.setUser(basicMatchPartyResourceAssembler.toResource(entity.getUserMatchParty()));
        resource.setOpponent(fullMatchPartyResourceAssembler.toResource(entity.getOpponentMatchParty()));
        resource.setDistance(entity.getDistance());
        return resource;
    }

    @Autowired
    public void setBasicMatchPartyResourceAssembler(BasicMatchPartyResourceAssembler basicMatchPartyResourceAssembler) {
        this.basicMatchPartyResourceAssembler = basicMatchPartyResourceAssembler;
    }

    @Autowired
    public void setFullMatchPartyResourceAssembler(FullMatchPartyResourceAssembler fullMatchPartyResourceAssembler) {
        this.fullMatchPartyResourceAssembler = fullMatchPartyResourceAssembler;
    }
}
