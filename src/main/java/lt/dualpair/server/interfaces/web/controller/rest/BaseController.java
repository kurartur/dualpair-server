package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {

    protected User getUserPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
