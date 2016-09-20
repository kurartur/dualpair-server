package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import lt.dualpair.server.service.user.SocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/user/{userId:[0-9]+}")
public class SociotypesController {

    private SocialUserService socialUserService;

    @RequestMapping(method = RequestMethod.PUT, value = "/sociotypes")
    public ResponseEntity setSociotypes(@PathVariable Long userId, @RequestBody String[] codes, @ActiveUser User principal) throws URISyntaxException {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        socialUserService.setUserSociotypes(principal.getId(), convertToEnumCodes(codes));
        return ResponseEntity.created(new URI("/api/user/" + userId)).build();
    }

    private Set<Sociotype.Code1> convertToEnumCodes(String[] codes) {
        Set<Sociotype.Code1> enumCodes = new HashSet<>();
        for (String code : codes) {
            Sociotype.Code1 enumCode = Sociotype.Code1.valueOf(code);
            enumCodes.add(enumCode);
        }
        return enumCodes;
    }

    @Autowired
    public void setSocialUserService(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }
}
