package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import org.springframework.social.connect.Connection;

public interface SocialUserService extends UserService {

    User loadOrCreate(Connection connection) throws SocialDataException;

    Photo addUserPhoto(Long userId, UserAccount.Type accountType, String idOnAccount, int position);

}
