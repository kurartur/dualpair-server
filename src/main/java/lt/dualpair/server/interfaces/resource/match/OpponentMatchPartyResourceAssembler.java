package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.match.MatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class OpponentMatchPartyResourceAssembler extends ResourceAssemblerSupport<MatchParty, OpponentMatchPartyResource> {

    private UserResourceAssembler userResourceAssembler;

    public OpponentMatchPartyResourceAssembler() {
        super(MatchController.class, OpponentMatchPartyResource.class);
    }

    @Override
    public OpponentMatchPartyResource toResource(MatchParty entity) {
        OpponentMatchPartyResource resource = new OpponentMatchPartyResource();
        resource.setPartyId(entity.getId());
        resource.add(linkTo(methodOn(MatchController.class).match(entity.getMatch().getId())).withRel("match"));
        resource.setUser(userResourceAssembler.toResource(entity.getUser()));
        return resource;
    }

    @Autowired
    public void setUserResourceAssembler(UserResourceAssembler userResourceAssembler) {
        this.userResourceAssembler = userResourceAssembler;
    }
}
