package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;
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
            locations.add(locationResource);
        }
        resource.setLocations(locations);

        List<PhotoResource> photos = new ArrayList<>();
        for (Photo photo : entity.getPhotos()) {
            PhotoResource photoResource = new PhotoResource();
            photoResource.setSourceUrl(photo.getSourceLink());
            photos.add(photoResource);
        }
        resource.setPhotos(photos);

        resource.add(linkTo(methodOn(SearchParametersController.class).getSearchParameters(entity.getId(), entity)).withRel("search-parameters"));

        return resource;
    }

    @Autowired
    public void setSociotypeResourceAssembler(SociotypeResourceAssembler sociotypeResourceAssembler) {
        this.sociotypeResourceAssembler = sociotypeResourceAssembler;
    }
}
