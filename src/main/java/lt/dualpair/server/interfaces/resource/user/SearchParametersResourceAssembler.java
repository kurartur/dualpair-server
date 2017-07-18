package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.match.SearchParameters;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class SearchParametersResourceAssembler extends ResourceAssemblerSupport<SearchParameters, SearchParametersResource> {

    public SearchParametersResourceAssembler() {
        super(UserController.class, SearchParametersResource.class);
    }

    @Override
    public SearchParametersResource toResource(SearchParameters entity) {
        SearchParametersResource resource = new SearchParametersResource();
        resource.setSearchMale(entity.getSearchMale());
        resource.setSearchFemale(entity.getSearchFemale());
        resource.setMinAge(entity.getMinAge());
        resource.setMaxAge(entity.getMaxAge());
        return resource;
    }
}
