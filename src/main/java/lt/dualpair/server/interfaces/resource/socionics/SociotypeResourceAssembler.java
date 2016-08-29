package lt.dualpair.server.interfaces.resource.socionics;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.interfaces.web.controller.rest.SocionicsController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class SociotypeResourceAssembler extends ResourceAssemblerSupport<Sociotype, SociotypeResource> {

    public SociotypeResourceAssembler() {
        super(SocionicsController.class, SociotypeResource.class);
    }

    @Override
    public SociotypeResource toResource(Sociotype entity) {
        SociotypeResource sociotypeResource = new SociotypeResource();
        sociotypeResource.setCode1(entity.getCode1().name());
        sociotypeResource.setCode2(entity.getCode2().name());
        return sociotypeResource;
    }
}
