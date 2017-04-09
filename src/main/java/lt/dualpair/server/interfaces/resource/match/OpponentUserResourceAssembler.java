package lt.dualpair.server.interfaces.resource.match;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.*;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResourceAssembler;
import lt.dualpair.server.interfaces.resource.user.LocationResource;
import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import lt.dualpair.server.interfaces.resource.user.UserAccountResource;
import lt.dualpair.server.interfaces.resource.user.UserResource;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OpponentUserResourceAssembler extends ResourceAssemblerSupport<OpponentUserResourceAssembler.AssemblingContext, UserResource> {

    private SociotypeResourceAssembler sociotypeResourceAssembler;
    private PhotoResourceAssembler photoResourceAssembler;

    public OpponentUserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(AssemblingContext context) {
        User entity = context.getUser();

        UserResource resource = new UserResource();

        resource.setUserId(entity.getId()); // TODO should we expose user id?
        resource.setName(entity.getName());
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

        List<Photo> sortedPhotos = new ArrayList<>(entity.getPhotos());
        Collections.sort(sortedPhotos, (o1, o2) -> o2.getPosition() - o1.getPosition());
        resource.setPhotos(photoResourceAssembler.toResources(sortedPhotos));

        if (context.isMutualMatch()) {
            List<UserAccountResource> accountResources = new ArrayList<>();
            for (UserAccount userAccount : entity.getUserAccounts()) {
                UserAccountResource userAccountResource = new UserAccountResource();
                userAccountResource.setAccountType(userAccount.getAccountType().getCode());
                userAccountResource.setAccountId(userAccount.getAccountId());
                accountResources.add(userAccountResource);
            }
            resource.setAccounts(accountResources);
        }

        if (entity.getRelationshipStatus() == RelationshipStatus.NONE) {
            resource.setRelationshipStatus("");
        } else {
            resource.setRelationshipStatus(entity.getRelationshipStatus().getCode());
        }

        Set<String> purposesOfBeing = new HashSet<>();
        for (PurposeOfBeing purposeOfBeing : entity.getPurposesOfBeing()) {
            purposesOfBeing.add(purposeOfBeing.getCode());
        }
        resource.setPurposesOfBeing(purposesOfBeing);

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

    public static class AssemblingContext {

        private User user;
        private boolean isMutualMatch;

        public AssemblingContext(User user, boolean isMutualMatch) {
            this.user = user;
            this.isMutualMatch = isMutualMatch;
        }

        public User getUser() {
            return user;
        }

        public boolean isMutualMatch() {
            return isMutualMatch;
        }
    }
}
