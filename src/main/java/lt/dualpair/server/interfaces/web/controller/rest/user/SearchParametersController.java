package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.SearchParameters;
import lt.dualpair.core.user.User;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResource;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResourceAssembler;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.security.UserDetails;
import lt.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping(value = "/api/user/{userId:[0-9]+}")
public class SearchParametersController {

    private UserService userService;
    private SearchParametersResourceAssembler searchParametersResourceAssembler;

    @RequestMapping(method = RequestMethod.PUT, value = "/search-parameters")
    public ResponseEntity setSearchParameters(@PathVariable Long userId, @RequestBody SearchParametersResource resource, @ActiveUser UserDetails principal) throws URISyntaxException {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(resource.getMinAge());
        searchParameters.setMaxAge(resource.getMaxAge());
        searchParameters.setSearchFemale(resource.getSearchFemale());
        searchParameters.setSearchMale(resource.getSearchMale());
        userService.setUserSearchParameters(userId, searchParameters);
        return ResponseEntity.created(new URI("/api/user")).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search-parameters")
    public ResponseEntity getSearchParameters(@PathVariable Long userId, @ActiveUser UserDetails principal) {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = userService.loadUserById(userId);
        return ResponseEntity.ok(searchParametersResourceAssembler.toResource(user.getSearchParameters()));
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setSearchParametersResourceAssembler(SearchParametersResourceAssembler searchParametersResourceAssembler) {
        this.searchParametersResourceAssembler = searchParametersResourceAssembler;
    }
}
