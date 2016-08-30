package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.interfaces.web.controller.rest.MatchController;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class BasicMatchPartyResourceAssembler extends ResourceAssemblerSupport<MatchParty, BasicMatchPartyResource> {

    public BasicMatchPartyResourceAssembler() {
        super(MatchController.class, BasicMatchPartyResource.class);
    }

    @Override
    public BasicMatchPartyResource toResource(MatchParty entity) {
        BasicMatchPartyResource resource = new BasicMatchPartyResource();
        resource.add(linkTo(methodOn(MatchController.class).match(entity.getMatch().getId())).withRel("match"));
        resource.add(linkTo(methodOn(UserController.class).getUser()).withRel("user"));
        resource.setResponse(entity.getResponse().name());
        return resource;
    }
}
