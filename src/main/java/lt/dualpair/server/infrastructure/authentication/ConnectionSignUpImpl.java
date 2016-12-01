package lt.dualpair.server.infrastructure.authentication;

import lt.dualpair.server.service.user.SocialDataException;
import lt.dualpair.server.service.user.SocialUserService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import javax.inject.Inject;

public class ConnectionSignUpImpl implements ConnectionSignUp {

    private SocialUserService socialUserService;

    @Inject
    public ConnectionSignUpImpl(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }

    @Override
    public String execute(Connection<?> connection) {
        try {
            return socialUserService.loadOrCreate(connection).getUsername();
        } catch (SocialDataException sce) {
            throw new RuntimeException(sce);
        }
    }

}
