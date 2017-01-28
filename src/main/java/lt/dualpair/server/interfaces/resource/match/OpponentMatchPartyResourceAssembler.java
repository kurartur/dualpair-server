package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.match.OpponentUserResourceAssembler.AssemblingContext;
import lt.dualpair.server.interfaces.web.controller.rest.match.MatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class OpponentMatchPartyResourceAssembler extends ResourceAssemblerSupport<MatchParty, OpponentMatchPartyResource> {

    private OpponentUserResourceAssembler opponentUserResourceAssembler;

    public OpponentMatchPartyResourceAssembler() {
        super(MatchController.class, OpponentMatchPartyResource.class);
    }

    @Override
    public OpponentMatchPartyResource toResource(MatchParty entity) {
        OpponentMatchPartyResource resource = new OpponentMatchPartyResource();
        resource.setPartyId(entity.getId());

        User user = entity.getUser();
        boolean isMatchMutual = entity.getMatch().isMutual();
        AssemblingContext assemblingContext = new AssemblingContext(user, isMatchMutual);
        resource.setUser(opponentUserResourceAssembler.toResource(assemblingContext));

        if (isMatchMutual) {
            resource.setResponse(entity.getResponse().name());
        }

        return resource;
    }

    @Autowired
    public void setOpponentUserResourceAssembler(OpponentUserResourceAssembler opponentUserResourceAssembler) {
        this.opponentUserResourceAssembler = opponentUserResourceAssembler;
    }
}
