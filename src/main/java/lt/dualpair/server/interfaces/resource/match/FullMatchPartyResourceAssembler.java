package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.MatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class FullMatchPartyResourceAssembler extends ResourceAssemblerSupport<MatchParty, FullMatchPartyResource> {

    private UserResourceAssembler userResourceAssembler;

    public FullMatchPartyResourceAssembler() {
        super(MatchController.class, FullMatchPartyResource.class);
    }

    @Override
    public FullMatchPartyResource toResource(MatchParty entity) {
        FullMatchPartyResource resource = new FullMatchPartyResource();
        resource.add(linkTo(methodOn(MatchController.class).match(entity.getMatch().getId())).withRel("match"));
        resource.setUser(userResourceAssembler.toResource(entity.getUser()));
        resource.setResponse(entity.getResponse().name());
        return resource;
    }

    @Autowired
    public void setUserResourceAssembler(UserResourceAssembler userResourceAssembler) {
        this.userResourceAssembler = userResourceAssembler;
    }
}
