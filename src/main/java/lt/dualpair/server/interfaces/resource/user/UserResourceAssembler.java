package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResource;
import lt.dualpair.server.interfaces.web.controller.rest.user.SearchParametersController;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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

        Set<LocationResource> locations = new HashSet<>();
        for (UserLocation userLocation : entity.getLocations()) {
            LocationResource locationResource = new LocationResource();
            locationResource.setCountryCode(userLocation.getCountryCode());
            locationResource.setCity(userLocation.getCity());
            locations.add(locationResource);
        }
        resource.setLocations(locations);

        Set<PhotoResource> photos = new HashSet<>();
        for (Photo photo : entity.getPhotos()) {
            PhotoResource photoResource = new PhotoResource();
            photoResource.setSourceUrl(photo.getSourceLink());
            photos.add(photoResource);
        }
        resource.setPhotos(photos);

        resource.add(linkTo(methodOn(SearchParametersController.class).getSearchParameters(entity.getId())).withRel("search-parameters"));

        return resource;
    }
}
