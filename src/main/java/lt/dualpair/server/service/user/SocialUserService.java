package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import org.springframework.social.connect.Connection;

public interface SocialUserService extends UserService {

    User loadOrCreate(Connection connection) throws SocialDataException;

    Photo addUserPhoto(Long userId, Photo photo);

}
