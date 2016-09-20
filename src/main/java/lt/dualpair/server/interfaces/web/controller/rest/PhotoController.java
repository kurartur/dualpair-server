package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import lt.dualpair.server.service.photo.PhotoService;
import lt.dualpair.server.service.photo.PhotoServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class PhotoController {

    private PhotoService photoService;

    @RequestMapping(method = RequestMethod.GET, value = "/photo/{photoId:[0-9]+}")
    public ResponseEntity photo(@PathVariable Long photoId, @ActiveUser User principal) throws PhotoServiceException, URISyntaxException {
        Photo photo = photoService.getUserPhoto(principal.getId(), photoId);
        if (photo != null) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(new URI(photo.getSourceLink())).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Autowired
    public void setPhotoService(PhotoService photoService) {
        this.photoService = photoService;
    }
}
