package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.geo.LocationProvider;
import lt.dualpair.server.domain.model.geo.LocationProviderException;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.LocationResource;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.BaseController;
import lt.dualpair.server.service.user.SocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController extends BaseController {

    private SocialUserService socialUserService;
    private LocationProvider locationProvider;
    private UserResourceAssembler userResourceAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    public UserResource getUser() {
        return userResourceAssembler.toResource(socialUserService.loadUserById(getUserPrincipal().getId()));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/user/{userId:[0-9]}")
    public ResponseEntity updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> data) throws URISyntaxException, ParseException {
        if (!getUserPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = socialUserService.loadUserById(userId);
        if (data.containsKey("description"))
            user.setDescription((String)data.get("description"));
        if (data.containsKey("name"))
            user.setName((String)data.get("name"));
        if (data.containsKey("dateOfBirth"))
            user.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String)data.get("dateOfBirth")));
        socialUserService.updateUser(user);
        return ResponseEntity.noContent().location(new URI("/api/user/" + userId)).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/date-of-birth")
    public ResponseEntity setDateOfBirth(@PathVariable Long userId, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth) throws URISyntaxException {
        if (!getUserPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        socialUserService.setUserDateOfBirth(userId, dateOfBirth);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI("/api/user")).build();
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

}
