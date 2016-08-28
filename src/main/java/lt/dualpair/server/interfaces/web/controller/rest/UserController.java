package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.geo.LocationProvider;
import lt.dualpair.server.domain.model.geo.LocationProviderException;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.dto.LocationDTO;
import lt.dualpair.server.interfaces.dto.SearchParametersDTO;
import lt.dualpair.server.interfaces.dto.SociotypeDTO;
import lt.dualpair.server.interfaces.dto.assembler.SearchParametersDTOAssembler;
import lt.dualpair.server.interfaces.dto.assembler.UserDTOAssembler;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
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
    private UserDTOAssembler userDTOAssembler;
    private SearchParametersDTOAssembler searchParametersDTOAssembler;
    private LocationProvider locationProvider;
    private UserResourceAssembler userResourceAssembler;

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    public UserResource getUser() {
        return userResourceAssembler.toResource(socialUserService.loadUserById(getUserPrincipal().getId()));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/sociotypes")
    public ResponseEntity setSociotypes(@RequestBody SociotypeDTO[] sociotypes) throws URISyntaxException {
        socialUserService.setUserSociotypes(getUserPrincipal().getId(), convertToCodes(sociotypes));
        return ResponseEntity.created(new URI("/api/user")).build();
    }

    private Set<Sociotype.Code1> convertToCodes(SociotypeDTO[] sociotypes) {
        Set<Sociotype.Code1> codes = new HashSet<>();
        for (SociotypeDTO sociotype : sociotypes) {
            Sociotype.Code1 code = Sociotype.Code1.valueOf(sociotype.getCode1());
            codes.add(code);
        }
        return codes;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/date-of-birth")
    public ResponseEntity setDateOfBirth(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth) throws URISyntaxException {
        socialUserService.setUserDateOfBirth(getUserPrincipal().getId(), dateOfBirth);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI("/api/user")).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/search-parameters")
    public ResponseEntity setSearchParameters(@RequestBody SearchParametersDTO searchParameters) throws URISyntaxException {
        socialUserService.setUserSearchParameters(getUserPrincipal().getId(), searchParametersDTOAssembler.toEntity(searchParameters));
        return ResponseEntity.created(new URI("/api/user")).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{userId:[0-9]+}/locations")
    public ResponseEntity setLocation(@RequestBody LocationDTO locationDTO, @PathVariable("userId") Long userId) throws LocationProviderException, URISyntaxException {
        if (!getUserPrincipal().getId().equals(userId)) {
            throw new ForbiddenException(ForbiddenException.illegalAccess);
        }

        Location location;
        if (locationDTO.getLatitude() != null && locationDTO.getLongitude() != null) {
            location = locationProvider.getLocation(locationDTO.getLatitude(), locationDTO.getLongitude());
        } else {
            throw new IllegalArgumentException("\"latitude\" and \"longitude\" must be provided");
        }

        socialUserService.addLocation(userId, location);

        return ResponseEntity.created(new URI("/api/user")).build();
    }

    private User getUserPrincipal() {
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Autowired
    public void setSocialUserService(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }

    @Autowired
    public void setUserDTOAssembler(UserDTOAssembler userDTOAssembler) {
        this.userDTOAssembler = userDTOAssembler;
    }

    @Autowired
    public void setSearchParametersDTOAssembler(SearchParametersDTOAssembler searchParametersDTOAssembler) {
        this.searchParametersDTOAssembler = searchParametersDTOAssembler;
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
