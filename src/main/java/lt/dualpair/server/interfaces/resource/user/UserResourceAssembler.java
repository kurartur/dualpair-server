package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResource;
import lt.dualpair.server.interfaces.web.controller.rest.UserController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User entity) {
        UserResource resource = new UserResource();
        resource.setUserId(entity.getId());
        resource.setName(entity.getName());
        resource.setDateOfBirth(entity.getDateOfBirth());
        resource.setAge(entity.getAge());
        resource.setDescription(entity.getDescription());

        Set<SociotypeResource> sociotypes = new HashSet<>();
        for (Sociotype sociotype : entity.getSociotypes()) {
            SociotypeResource sociotypeResource = new SociotypeResource();
            sociotypeResource.setCode1(sociotype.getCode1().name());
            sociotypeResource.setCode2(sociotype.getCode2().name());
            sociotypes.add(sociotypeResource);
        }
        resource.setSociotypes(sociotypes);

        return resource;
    }
}
