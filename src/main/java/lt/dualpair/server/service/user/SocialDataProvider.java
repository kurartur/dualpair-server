package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.User;

import java.util.List;
import java.util.Optional;

public interface SocialDataProvider {

    String getAccountId();

    User enhanceUser(User user) throws SocialDataException;

    List<Photo> getPhotos();

    List<Photo> getPhotos(List<String> ids);

    Optional<Photo> getPhoto(String idOnAccount);

}
