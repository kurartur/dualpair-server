package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.web.controller.rest.match.MatchPartyController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class MatchPartyUserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

    public MatchPartyUserResourceAssembler() {
        super(MatchPartyController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User entity) {
        return null;
    }
}
