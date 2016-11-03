package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.interfaces.web.controller.rest.user.UserPhotoController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PhotoResourceAssembler extends ResourceAssemblerSupport<Photo, PhotoResource> {

    public PhotoResourceAssembler() {
        super(UserPhotoController.class, PhotoResource.class);
    }

    @Override
    public PhotoResource toResource(Photo entity) {
        PhotoResource resource = new PhotoResource();
        resource.setPhotoId(entity.getId());
        resource.setAccountType(entity.getAccountType().getCode());
        resource.setIdOnAccount(entity.getIdOnAccount());
        resource.setSourceUrl(entity.getSourceLink());
        resource.setPosition(entity.getPosition());
        return resource;
    }
}
