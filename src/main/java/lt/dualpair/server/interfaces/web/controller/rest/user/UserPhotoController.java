package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import lt.dualpair.server.service.user.SocialDataProvider;
import lt.dualpair.server.service.user.SocialDataProviderFactory;
import lt.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/{userId:[0-9]+}")
public class UserPhotoController {

    private SocialDataProviderFactory socialDataProviderFactory;
    private PhotoResourceAssembler photoResourceAssembler;
    private UserService userService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/photos/{photoId:[0-9]+}")
    public ResponseEntity deletePhoto(@PathVariable Long userId, @PathVariable Long photoId, @ActiveUser User principal) {
        if (!userId.equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.deleteUserPhoto(userId, photoId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/available-photos")
    public ResponseEntity getAvailablePhotos(Long userId, @RequestParam("at") String accountType, @ActiveUser User principal) {
        if (!userId.equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SocialDataProvider socialDataProvider = socialDataProviderFactory.getProvider(UserAccount.Type.fromCode(accountType), userId);
        return ResponseEntity.ok(photoResourceAssembler.toResources(socialDataProvider.getPhotos()));
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
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
