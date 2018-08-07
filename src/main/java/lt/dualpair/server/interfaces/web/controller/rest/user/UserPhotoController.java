package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.dualpair.core.photo.Photo;
import lt.dualpair.server.interfaces.resource.user.PhotoResource;
import lt.dualpair.server.interfaces.resource.user.PhotoResourceAssembler;
import lt.dualpair.server.service.user.PhotoFileHelper;
import lt.dualpair.server.service.user.PhotoModel;
import lt.dualpair.server.service.user.UserPhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user/{userId:[0-9]+}")
public class UserPhotoController {

    private PhotoResourceAssembler photoResourceAssembler;
    private UserPhotoService userPhotoService;
    private PhotoFileHelper photoFileHelper;

    @Inject
    public UserPhotoController(PhotoResourceAssembler photoResourceAssembler, UserPhotoService userPhotoService, PhotoFileHelper photoFileHelper) {
        this.photoResourceAssembler = photoResourceAssembler;
        this.userPhotoService = userPhotoService;
        this.photoFileHelper = photoFileHelper;
    }

    @PreAuthorize("@authorizer.hasPermission(authentication, #userId)")
    @PostMapping("/photos")
    public ResponseEntity setPhotos(
            @PathVariable Long userId,
            @RequestPart(value = "photoFiles") MultipartFile[] photoFiles,
            @RequestPart(value = "data") String dataJson) throws Exception {
        PhotoResourceCollection data = new ObjectMapper().readValue(dataJson, PhotoResourceCollection.class);
        List<Photo> photos = userPhotoService.setUserPhotos(userId, createModels(photoFiles, data.getPhotoResources()));
        return ResponseEntity.ok(photoResourceAssembler.toResources(photos));
    }

    @GetMapping("/photo")
    public ResponseEntity readPhoto(@PathVariable Long userId, @RequestParam String name) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(photoFileHelper.read(userId, name));
    }

    private List<PhotoModel> createModels(MultipartFile[] photoFiles, List<PhotoResource> photoResources) throws IOException {
        List<PhotoModel> models = new ArrayList<>();
        for (PhotoResource photoResource : photoResources) {
            if (photoResource.getPhotoId() == null) {
                for (MultipartFile photoFile : photoFiles) {
                    if (photoFile.getOriginalFilename().equals(photoResource.getSource())) {
                        models.add(new PhotoModel(null, photoFile.getBytes(), photoResource.getPosition()));
                    }
                }
            } else {
                models.add(new PhotoModel(photoResource.getPhotoId(), null, photoResource.getPosition()));
            }
        }
        if (models.size() != photoResources.size()) {
            throw new IllegalArgumentException("Something wrong");
        }
        return models;
    }

    public static final class PhotoResourceCollection {

        List<PhotoResource> photoResources;

        public List<PhotoResource> getPhotoResources() {
            return photoResources;
        }

        public void setPhotoResources(List<PhotoResource> photoResources) {
            this.photoResources = photoResources;
        }

    }
}
