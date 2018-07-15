package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.Response;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.core.user.UserResponse;
import lt.dualpair.server.interfaces.resource.user.UserResponseResourceAssembler;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.security.UserDetails;
import lt.dualpair.server.service.user.UserResponseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Random;

@RestController
@RequestMapping("/api/user/{userId:[0-9]+}/responses")
public class UserResponseController {

    private UserResponseService userResponseService;
    private UserResponseResourceAssembler userResponseResourceAssembler;
    private boolean fakeMatches;
    private UserRepository userRepository;

    @Inject
    public UserResponseController(UserResponseService userResponseService,
                                  UserResponseResourceAssembler userResponseResourceAssembler,
                                  @Value("${fakeMatches}") boolean fakeMatches, UserRepository userRepository) {
        this.userResponseService = userResponseService;
        this.userResponseResourceAssembler = userResponseResourceAssembler;
        this.fakeMatches = fakeMatches;
        this.userRepository = userRepository;
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

        if (fakeMatches) {
            User toUser = userRepository.findById(toUserId).orElseThrow(() -> new IllegalArgumentException("User " + toUserId + " not found"));
            String description = toUser.getDescription();
            if (!StringUtils.isEmpty(description) && description.startsWith("Lorem ipsum") && description.endsWith("FAKE")) {
                userResponseService.respond(toUserId, userId, new Random().nextInt(2) == 1 ? Response.YES : Response.NO);
            }
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity responses(@PathVariable Long userId,
                                    Pageable pageable,
                                    PagedResourcesAssembler<UserResponse> pagedResourcesAssembler,
                                    @ActiveUser UserDetails principal) {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<UserResponse> page = userResponseService.getResponsesPage(userId, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(page, userResponseResourceAssembler));
    }
}
