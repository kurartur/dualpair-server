package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResource;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.BaseController;
import lt.dualpair.server.service.user.SocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping(value = "/api/user/{userId:[0-9]+}")
public class SearchParametersController extends BaseController {

    private SocialUserService socialUserService;
    private SearchParametersResourceAssembler searchParametersResourceAssembler;

    @RequestMapping(method = RequestMethod.PUT, value = "/search-parameters")
    public ResponseEntity setSearchParameters(@PathVariable Long userId, @RequestBody SearchParametersResource resource) throws URISyntaxException {
        if (!getUserPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(resource.getMinAge());
        searchParameters.setMaxAge(resource.getMaxAge());
        searchParameters.setSearchFemale(resource.getSearchFemale());
        searchParameters.setSearchMale(resource.getSearchMale());
        socialUserService.setUserSearchParameters(userId, searchParameters);
        return ResponseEntity.created(new URI("/api/user")).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search-parameters")
    public ResponseEntity getSearchParameters(@PathVariable Long userId) {
        if (!getUserPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = socialUserService.loadUserById(userId);
        return ResponseEntity.ok(searchParametersResourceAssembler.toResource(user.getSearchParameters()));
    }

    @Autowired
    public void setSocialUserService(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }

    @Autowired
    public void setSearchParametersResourceAssembler(SearchParametersResourceAssembler searchParametersResourceAssembler) {
        this.searchParametersResourceAssembler = searchParametersResourceAssembler;
    }
}
