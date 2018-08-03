package lt.dualpair.server.service.user;

public interface UserMatchService {

    void remove(Long matchId, Long userId);

    void unmatch(Long matchId, Long userId);

}
