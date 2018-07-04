package lt.dualpair.server.service.user;

import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRequest;

import java.util.Optional;

public interface UserSearchService {

    Optional<User> findOne(UserRequest userRequest);

}
