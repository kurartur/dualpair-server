package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.core.match.MatchParty;
import lt.dualpair.core.user.User;
import lt.dualpair.server.interfaces.resource.match.OpponentUserResourceAssembler.AssemblingContext;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserSearchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class OpponentMatchPartyResourceAssembler extends ResourceAssemblerSupport<MatchParty, OpponentMatchPartyResource> {

    private OpponentUserResourceAssembler opponentUserResourceAssembler;

    public OpponentMatchPartyResourceAssembler() {
        super(UserSearchController.class, OpponentMatchPartyResource.class);
    }

    @Override
    public OpponentMatchPartyResource toResource(MatchParty entity) {
        OpponentMatchPartyResource resource = new OpponentMatchPartyResource();
        resource.setPartyId(entity.getId());

        User user = entity.getUser();
        AssemblingContext assemblingContext = new AssemblingContext(user, true);
        resource.setUser(opponentUserResourceAssembler.toResource(assemblingContext));

        return resource;
    }

    @Autowired
    public void setOpponentUserResourceAssembler(OpponentUserResourceAssembler opponentUserResourceAssembler) {
        this.opponentUserResourceAssembler = opponentUserResourceAssembler;
    }
}
