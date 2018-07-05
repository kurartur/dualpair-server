package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.user.UserResponse;
import lt.dualpair.server.interfaces.resource.match.OpponentUserResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserResponseController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class UserResponseResourceAssembler extends ResourceAssemblerSupport<UserResponse, UserResponseResource> {

    private OpponentUserResourceAssembler opponentUserResourceAssembler;

    @Inject
    public UserResponseResourceAssembler(OpponentUserResourceAssembler opponentUserResourceAssembler) {
        super(UserResponseController.class, UserResponseResource.class);
        this.opponentUserResourceAssembler = opponentUserResourceAssembler;
    }

    @Override
    public UserResponseResource toResource(UserResponse entity) {
        UserResponseResource resource = new UserResponseResource();
        resource.setResponse(entity.getResponse().getCode());
        resource.setUser(opponentUserResourceAssembler.toResource(new OpponentUserResourceAssembler.AssemblingContext(entity.getToUser(), entity.getMatch() != null)));
        return resource;
    }
}
