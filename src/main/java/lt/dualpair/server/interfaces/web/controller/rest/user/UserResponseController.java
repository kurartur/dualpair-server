package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.Response;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.security.UserDetails;
import lt.dualpair.server.service.user.UserResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/api/user/{userId:[0-9]+}/responses")
public class UserResponseController {

    private UserResponseService userResponseService;

    @Inject
    public UserResponseController(UserResponseService userResponseService) {
        this.userResponseService = userResponseService;
    }

    @PutMapping
    public ResponseEntity respond(@PathVariable Long userId,
                                  @RequestParam Long toUserId,
                                  @RequestParam(name = "response") String responseString,
                                  @ActiveUser UserDetails principal) {

        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Response response = Response.valueOf(responseString);
        userResponseService.respond(userId, toUserId, response);
        return ResponseEntity.ok().build();
    }

}
