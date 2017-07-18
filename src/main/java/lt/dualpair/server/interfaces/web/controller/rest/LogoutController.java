package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.core.user.User;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity logout(@ActiveUser User user) {
        return ResponseEntity.ok().build();
    }

}
