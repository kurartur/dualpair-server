package lt.dualpair.server.service.user;

import lt.dualpair.core.user.Response;
import lt.dualpair.core.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserResponseService {

    void respond(Long userId, Long toUserId, Response response);

    Page<UserResponse> getResponsesPage(Long userId, Pageable pageable);

}
