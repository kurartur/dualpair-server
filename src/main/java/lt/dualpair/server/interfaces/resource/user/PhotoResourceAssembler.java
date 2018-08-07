package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserPhotoController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class PhotoResourceAssembler extends ResourceAssemblerSupport<Photo, PhotoResource> {

    public PhotoResourceAssembler() {
        super(UserPhotoController.class, PhotoResource.class);
    }

    @Override
    public PhotoResource toResource(Photo entity) {
        PhotoResource resource = new PhotoResource();
        resource.setPhotoId(entity.getId());
        String source = entity.getSourceLink();
        if (source.contains("http")) {
            resource.setSource(source);
        } else {
            resource.setSource(linkTo(methodOn(UserPhotoController.class).readPhoto(entity.getUser().getId(), entity.getSourceLink())).toString());
        }
        resource.setPosition(entity.getPosition());
        return resource;
    }
}
