package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.user.PurposeOfBeing;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserAccount;
import lt.dualpair.core.user.UserLocation;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.user.SearchParametersController;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

    private SociotypeResourceAssembler sociotypeResourceAssembler;
    private PhotoResourceAssembler photoResourceAssembler;

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
        resource.setSociotypes(new HashSet<>(sociotypeResourceAssembler.toResources(entity.getSociotypes())));

        Set<LocationResource> locations = new HashSet<>();
        for (UserLocation userLocation : entity.getLocations()) {
            LocationResource locationResource = new LocationResource();
            locationResource.setCountryCode(userLocation.getCountryCode());
            locationResource.setCity(userLocation.getCity());
            locationResource.setLatitude(userLocation.getLatitude());
            locationResource.setLongitude(userLocation.getLongitude());
            locations.add(locationResource);
        }
        resource.setLocations(locations);

        resource.setPhotos(photoResourceAssembler.toResources(entity.getPhotos()));

        List<UserAccountResource> userAccounts = new ArrayList<>();
        for (UserAccount userAccount : entity.getUserAccounts()) {
            UserAccountResource accountResource = new UserAccountResource();
            accountResource.setAccountType(userAccount.getAccountType().getCode());
            accountResource.setAccountId(userAccount.getAccountId());
            userAccounts.add(accountResource);
        }
        resource.setAccounts(userAccounts);

        resource.setRelationshipStatus(entity.getRelationshipStatus().getCode());

        Set<String> purposesOfBeing = new HashSet<>();
        for (PurposeOfBeing purposeOfBeing : entity.getPurposesOfBeing()) {
            purposesOfBeing.add(purposeOfBeing.getCode());
        }
        resource.setPurposesOfBeing(purposesOfBeing);

        // TODO null as param
        resource.add(linkTo(methodOn(SearchParametersController.class).getSearchParameters(entity.getId(), null)).withRel("search-parameters"));

        return resource;
    }

    @Autowired
    public void setSociotypeResourceAssembler(SociotypeResourceAssembler sociotypeResourceAssembler) {
        this.sociotypeResourceAssembler = sociotypeResourceAssembler;
    }

    @Autowired
    public void setPhotoResourceAssembler(PhotoResourceAssembler photoResourceAssembler) {
        this.photoResourceAssembler = photoResourceAssembler;
    }
}
