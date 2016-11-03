package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import lt.dualpair.server.interfaces.resource.user.PhotoResource;
import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import lt.dualpair.server.service.user.SocialDataProvider;
import lt.dualpair.server.service.user.SocialDataProviderFactory;
import lt.dualpair.server.service.user.SocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/{userId:[0-9]+}")
public class UserPhotoController {

    private SocialDataProviderFactory socialDataProviderFactory;
    private PhotoResourceAssembler photoResourceAssembler;
    private SocialUserService socialUserService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/photos/{photoId:[0-9]+}")
    public ResponseEntity deletePhoto(@PathVariable Long userId, @PathVariable Long photoId, @ActiveUser User principal) {
        if (!userId.equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        socialUserService.deleteUserPhoto(userId, photoId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/available-photos")
    public ResponseEntity getAvailablePhotos(@PathVariable Long userId, @RequestParam("at") String accountType, @ActiveUser User principal) {
        if (!userId.equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SocialDataProvider socialDataProvider = socialDataProviderFactory.getProvider(UserAccount.Type.fromCode(accountType), principal.getUsername());
        return ResponseEntity.ok(photoResourceAssembler.toResources(socialDataProvider.getPhotos()));
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/photos")
    public ResponseEntity addPhoto(@PathVariable Long userId, @RequestBody PhotoResource photoResource, @ActiveUser User principal) {
        if (!userId.equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Photo photo = socialUserService.addUserPhoto(userId,
                UserAccount.Type.fromCode(photoResource.getAccountType()),
                photoResource.getIdOnAccount(),
                photoResource.getPosition());
        return ResponseEntity.status(HttpStatus.CREATED).body(photoResourceAssembler.toResource(photo));
    }

    @Autowired
    public void setSocialDataProviderFactory(SocialDataProviderFactory socialDataProviderFactory) {
        this.socialDataProviderFactory = socialDataProviderFactory;
    }

    @Autowired
    public void setPhotoResourceAssembler(PhotoResourceAssembler photoResourceAssembler) {
        this.photoResourceAssembler = photoResourceAssembler;
    }

    @Autowired
    public void setSocialUserService(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }
}
