package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;

import java.util.List;

public interface SocialDataProvider {

    String getAccountId();

    User enhanceUser(User user) throws SocialDataException;

    List<Photo> getPhotos();

}
