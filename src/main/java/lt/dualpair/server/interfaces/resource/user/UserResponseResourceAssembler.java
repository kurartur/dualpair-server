package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.user.UserResponse;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserMatchController;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserResponseController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserResponseResourceAssembler extends ResourceAssemblerSupport<UserResponse, UserResponseResource> {

    private UserResourceAssembler userResourceAssembler;

    @Inject
    public UserResponseResourceAssembler(UserResourceAssembler userResourceAssembler) {
        super(UserResponseController.class, UserResponseResource.class);
        this.userResourceAssembler = userResourceAssembler;
    }

    @Override
    public UserResponseResource toResource(UserResponse entity) {
        boolean isMatch = entity.getMatch() != null;

        UserResponseResource resource = new UserResponseResource();
        resource.setResponse(entity.getResponse().getCode());
        resource.setUser(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(entity.getToUser(), isMatch, false)));
        resource.setMatch(isMatch);
        resource.setDate(entity.getDate());

        if (isMatch) {
            resource.add(linkTo(methodOn(UserMatchController.class).getMatch(entity.getUser().getId(), entity.getMatch().getId(), null)).withRel("match"));
        }

        return resource;
    }
}
