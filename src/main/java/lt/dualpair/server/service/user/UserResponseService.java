package lt.dualpair.server.service.user;

import lt.dualpair.core.user.Response;

public interface UserResponseService {

    void respond(Long userId, Long toUserId, Response response);

}
