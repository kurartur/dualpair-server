package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.location.Location;
import lt.dualpair.core.location.LocationProvider;
import lt.dualpair.core.location.LocationProviderException;
import lt.dualpair.core.user.*;
import lt.dualpair.server.interfaces.resource.user.LocationResource;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.security.UserDetails;
import lt.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;
    private LocationProvider locationProvider;
    private UserResourceAssembler userResourceAssembler;
    private UserResponseRepository userResponseRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    public ResponseEntity me(@ActiveUser UserDetails principal) {
        User user = userService.loadUserById(principal.getId());
        return ResponseEntity.ok(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(user, true, true)));
    }

    @RequestMapping(method = RequestMethod.GET, value="/user/{userId:[0-9]+}")
    public ResponseEntity getUser(@PathVariable Long userId, @ActiveUser UserDetails principal) {
        boolean isMatch = false;
        boolean isPrincipal = userId.equals(principal.getId());
        if (!isPrincipal) {
            Optional<UserResponse> response = userResponseRepository.findByParties(principal.getId(), userId);
            if (!response.isPresent()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            isMatch = response.get().getMatch() != null;
        }
        User user = userService.loadUserById(userId);
        UserResource userResource = userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(user, isMatch, isPrincipal));
        return ResponseEntity.ok(userResource);
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/user/{userId:[0-9]+}")
    public ResponseEntity updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> data, @ActiveUser UserDetails principal) throws URISyntaxException, ParseException {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = userService.loadUserById(userId);
        if (data.containsKey("description"))
            user.setDescription((String)data.get("description"));
        if (data.containsKey("name"))
            user.setName((String)data.get("name"));
        if (data.containsKey("dateOfBirth"))
            user.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String)data.get("dateOfBirth")));
        if (data.containsKey("relationshipStatus")) {
            if (StringUtils.isEmpty(data.get("relationshipStatus"))) {
                user.setRelationshipStatus(RelationshipStatus.NONE);
            } else {
                user.setRelationshipStatus(RelationshipStatus.fromCode((String)data.get("relationshipStatus")));
            }
        }
        if (data.containsKey("purposesOfBeing")) {
            Set<PurposeOfBeing> purposesOfBeing = new HashSet<>();
            for (String purpose : (List<String>) data.get("purposesOfBeing")) {
                purposesOfBeing.add(PurposeOfBeing.fromCode(purpose));
            }
            user.setPurposesOfBeing(purposesOfBeing);
        }
        userService.updateUser(user);
        return ResponseEntity.noContent().location(new URI("/api/user/" + userId)).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/date-of-birth")
    public ResponseEntity setDateOfBirth(@PathVariable Long userId, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth, @ActiveUser UserDetails principal) throws URISyntaxException {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.setUserDateOfBirth(userId, dateOfBirth);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI("/api/user")).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/locations")
    public ResponseEntity setLocation(@RequestBody LocationResource locationResource, @PathVariable("userId") Long userId, @ActiveUser UserDetails principal) throws LocationProviderException, URISyntaxException {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Location location;
        if (locationResource.getLatitude() != null && locationResource.getLongitude() != null) {
            location = locationProvider.getLocation(locationResource.getLatitude(), locationResource.getLongitude());
        } else {
            throw new IllegalArgumentException("\"latitude\" and \"longitude\" must be provided");
        }

        userService.addLocation(userId, location);

        return ResponseEntity.created(new URI("/api/user")).build();
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
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
    public void setUserResponseRepository(UserResponseRepository userResponseRepository) {
        this.userResponseRepository = userResponseRepository;
    }
}
