package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.vkontakte.api.VKontakte;

import java.util.ArrayList;
import java.util.List;

public class VKontakteDataProvider implements SocialDataProvider {

    private static final Logger logger = Logger.getLogger(FacebookDataProvider.class.getName());

    private Connection<? extends VKontakte> vkontakteConnection;

    public VKontakteDataProvider(Connection<? extends VKontakte> vkontakteConnection) {
        this.vkontakteConnection = vkontakteConnection;
    }

    @Override
    public String getAccountId() {
        return null;
    }

    @Override
    public User enhanceUser(User user) throws SocialDataException {
        return null;
    }

    @Override
    public List<Photo> getPhotos() {
        return new ArrayList<>();
    }
}
