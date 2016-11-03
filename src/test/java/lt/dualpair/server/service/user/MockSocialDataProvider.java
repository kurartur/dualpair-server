package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("it")
public class MockSocialDataProvider implements SocialDataProvider {

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
        return null;
    }

    @Override
    public Optional<Photo> getPhoto(String idOnAccount) {
        return null;
    }
}
