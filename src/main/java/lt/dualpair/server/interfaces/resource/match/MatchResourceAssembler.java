package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.core.match.UserAwareMatch;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserSearchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class MatchResourceAssembler extends ResourceAssemblerSupport<UserAwareMatch, MatchResource> {

    private UserResourceAssembler userResourceAssembler;

    public MatchResourceAssembler() {
        super(UserSearchController.class, MatchResource.class);
    }

    @Override
    public MatchResource toResource(UserAwareMatch entity) {
        MatchResource resource = new MatchResource();
        resource.setMatchId(entity.getId());
        resource.setUser(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(entity.getOpponentMatchParty().getUser(), true, false)));
        resource.setDate(entity.getDate());
        return resource;
    }

    @Autowired
    public void setUserResourceAssembler(UserResourceAssembler userResourceAssembler) {
        this.userResourceAssembler = userResourceAssembler;
    }
}
