package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.UserAccount;

import java.util.List;

public interface SocialUserService extends UserService {

    Photo addUserPhoto(Long userId, UserAccount.Type accountType, String idOnAccount, int position);

    List<Photo> setUserPhotos(Long userId, List<PhotoData> photoDataList);

    class PhotoData {

        public UserAccount.Type accountType;
        public String idOnAccount;
        public int position;

        public UserAccount.Type getAccountType() {
            return accountType;
        }
    }

}
