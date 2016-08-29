package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.geo.LocationProvider;
import lt.dualpair.server.domain.model.geo.LocationProviderException;
import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.*;
import lt.dualpair.server.service.user.SocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {

    private SocialUserService socialUserService;
    private LocationProvider locationProvider;
    private UserResourceAssembler userResourceAssembler;
    private SearchParametersResourceAssembler searchParametersResourceAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    public UserResource getUser() {
        return userResourceAssembler.toResource(socialUserService.loadUserById(getUserPrincipal().getId()));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/sociotypes")
    public ResponseEntity setSociotypes(@PathVariable Long userId, @RequestBody String[] codes) throws URISyntaxException {
        if (!getUserPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        socialUserService.setUserSociotypes(getUserPrincipal().getId(), convertToEnumCodes(codes));
        return ResponseEntity.created(new URI("/api/user")).build();
    }

    private Set<Sociotype.Code1> convertToEnumCodes(String[] codes) {
        Set<Sociotype.Code1> enumCodes = new HashSet<>();
        for (String code : codes) {
            Sociotype.Code1 enumCode = Sociotype.Code1.valueOf(code);
            enumCodes.add(enumCode);
        }
        return enumCodes;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/date-of-birth")
    public ResponseEntity setDateOfBirth(@PathVariable Long userId, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth) throws URISyntaxException {
        if (!getUserPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        socialUserService.setUserDateOfBirth(userId, dateOfBirth);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI("/api/user")).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/search-parameters")
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

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/locations")
    public ResponseEntity setLocation(@RequestBody LocationResource locationResource, @PathVariable("userId") Long userId) throws LocationProviderException, URISyntaxException {
        if (!getUserPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Location location;
        if (locationResource.getLatitude() != null && locationResource.getLongitude() != null) {
            location = locationProvider.getLocation(locationResource.getLatitude(), locationResource.getLongitude());
        } else {
            throw new IllegalArgumentException("\"latitude\" and \"longitude\" must be provided");
        }

        socialUserService.addLocation(userId, location);

        return ResponseEntity.created(new URI("/api/user")).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{userId:[0-9]+}/search-parameters")
    public ResponseEntity getSearchParameters(@PathVariable Long userId) {
        if (!getUserPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = socialUserService.loadUserById(userId);
        return ResponseEntity.ok(searchParametersResourceAssembler.toResource(user.getSearchParameters()));
    }

    private User getUserPrincipal() {
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Autowired
    public void setSocialUserService(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }

    @Autowired
    @Qualifier("multipleServiceLocationProvider")
    public void setLocationProvider(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }

    @Autowired
    public void setUserResourceAssembler(UserResourceAssembler userResourceAssembler) {
        this.userResourceAssembler = userResourceAssembler;
    }

    @Autowired
    public void setSearchParametersResourceAssembler(SearchParametersResourceAssembler searchParametersResourceAssembler) {
        this.searchParametersResourceAssembler = searchParametersResourceAssembler;
    }
}
