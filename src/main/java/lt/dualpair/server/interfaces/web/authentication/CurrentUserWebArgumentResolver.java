package lt.dualpair.server.interfaces.web.authentication;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

public class CurrentUserWebArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isSocialUserDetails = parameter.getParameterType().equals(SocialUserDetails.class);
        boolean isUserDetails = parameter.getParameterType().equals(UserDetails.class);
        boolean isDualpairUserDetails = parameter.getParameterType().equals(lt.dualpair.server.security.UserDetails.class);
        return (isSocialUserDetails || isUserDetails || isDualpairUserDetails) && parameter.hasParameterAnnotation(ActiveUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Principal principal = webRequest.getUserPrincipal();
        return ((Authentication) principal).getPrincipal();
    }

}
