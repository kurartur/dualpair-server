package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.*;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResourceAssembler;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserResourceAssembler extends ResourceAssemblerSupport<UserResourceAssembler.AssemblingContext, UserResource> {

    private SociotypeResourceAssembler sociotypeResourceAssembler;
    private PhotoResourceAssembler photoResourceAssembler;

    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(AssemblingContext context) {
        User entity = context.getUser();

        UserResource resource = new UserResource();

        resource.setUserId(entity.getId());
        resource.setName(entity.getName());
        if (context.isPrincipal()) {
            resource.setDateOfBirth(entity.getDateOfBirth());
        }
        resource.setAge(entity.getAge());
        resource.setGender(entity.getGender().getCode());
        resource.setDescription(entity.getDescription());
        resource.setSociotypes(new HashSet<>(sociotypeResourceAssembler.toResources(entity.getSociotypes())));

        Set<LocationResource> locations = new HashSet<>();
        for (UserLocation userLocation : entity.getLocations()) {
            LocationResource locationResource = new LocationResource();
            locationResource.setCountryCode(userLocation.getCountryCode());
            locationResource.setCity(userLocation.getCity());
            locationResource.setLatitude(userLocation.getLatitude()); // TODO obscure, point to center of city or smth
            locationResource.setLongitude(userLocation.getLongitude());
            locations.add(locationResource);
        }
        resource.setLocations(locations);

        List<Photo> sortedPhotos = new ArrayList<>(entity.getPhotos());
        Collections.sort(sortedPhotos, (o1, o2) -> o2.getPosition() - o1.getPosition());
        resource.setPhotos(photoResourceAssembler.toResources(sortedPhotos));

        if (context.isMutualMatch() || context.isPrincipal()) {
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
        private boolean isPrincipal;

        public AssemblingContext(User user, boolean isMutualMatch, boolean isPrincipal) {
            this.user = user;
            this.isMutualMatch = isMutualMatch;
            this.isPrincipal = isPrincipal;
        }

        public User getUser() {
            return user;
        }

        public boolean isMutualMatch() {
            return isMutualMatch;
        }

        public boolean isPrincipal() {
            return isPrincipal;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssemblingContext that = (AssemblingContext) o;
            return isMutualMatch == that.isMutualMatch &&
                    isPrincipal == that.isPrincipal &&
                    Objects.equals(user, that.user);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, isMutualMatch, isPrincipal);
        }
    }
}
