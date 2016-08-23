package lt.dualpair.server.interfaces.web.controller;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.service.user.SocialDataException;
import lt.dualpair.server.service.user.SocialUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class SignupController {

    private ProviderSignInUtils providerSignInUtils;

    private SocialUserService socialUserService;

    @Inject
    public SignupController(ConnectionFactoryLocator connectionFactoryLocator,
                            UsersConnectionRepository connectionRepository,
                            SocialUserService socialUserService) {
        this.providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
        this.socialUserService = socialUserService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/signup")
    public String signup(NativeWebRequest request) throws IOException {
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        User user;
        try {
            user = socialUserService.loadOrCreate(connection);
        } catch (SocialDataException sce) {
            // TODO throw
            throw new RuntimeException(sce);
        }
        SocialAuthenticationToken socialAuthenticationToken = new SocialAuthenticationToken(connection, user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(socialAuthenticationToken);
        providerSignInUtils.doPostSignUp(user.getUserId(), request);

        HttpServletRequest req = (HttpServletRequest)request.getNativeRequest();
        HttpServletResponse resp = (HttpServletResponse)request.getNativeResponse();
        RequestCache rc = new HttpSessionRequestCache();
        SavedRequest savedRequest = rc.getRequest(req, resp);
        String targetUrl = savedRequest.getRedirectUrl();
        if(targetUrl != null) {
            return "redirect:" + targetUrl;
        }
        return "redirect:/";
    }

}
