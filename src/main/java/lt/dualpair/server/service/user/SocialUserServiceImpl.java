package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Supplier;

@Service("socialUserService")
public class SocialUserServiceImpl extends UserServiceImpl implements SocialUserService {

    private static final Logger logger = LoggerFactory.getLogger(SocialUserServiceImpl.class);

    private SocialDataProviderFactory socialDataProviderFactory;

    @Override
    @Transactional
    public Photo addUserPhoto(Long userId, UserAccount.Type accountType, String idOnAccount, int position) {
        User user = loadUserById(userId);
        Photo photo = socialDataProviderFactory.getProvider(accountType, user.getUsername())
                .getPhoto(idOnAccount).orElseThrow((Supplier<RuntimeException>) () -> new IllegalArgumentException("Photo doesn't exist on account or is not public"));
        photo.setUser(user);
        photo.setPosition(position);
        photoRepository.save(photo);
        return photo;
    }

    @Autowired
    public void setSocialDataProviderFactory(SocialDataProviderFactory socialDataProviderFactory) {
        this.socialDataProviderFactory = socialDataProviderFactory;
    }
}
