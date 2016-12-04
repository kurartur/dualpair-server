package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.UserAccount;

public interface SocialUserService extends UserService {

    Photo addUserPhoto(Long userId, UserAccount.Type accountType, String idOnAccount, int position);

}
