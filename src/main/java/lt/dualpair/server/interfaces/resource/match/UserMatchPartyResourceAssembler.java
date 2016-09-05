package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.interfaces.web.controller.rest.match.MatchController;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserMatchPartyResourceAssembler extends ResourceAssemblerSupport<MatchParty, UserMatchPartyResource> {

    public UserMatchPartyResourceAssembler() {
        super(MatchController.class, UserMatchPartyResource.class);
    }

    @Override
    public UserMatchPartyResource toResource(MatchParty entity) {
        UserMatchPartyResource resource = new UserMatchPartyResource();
        resource.setPartyId(entity.getId());
        resource.add(linkTo(methodOn(UserController.class).getUser()).withRel("user"));
        resource.setResponse(entity.getResponse().name());
        return resource;
    }
}
