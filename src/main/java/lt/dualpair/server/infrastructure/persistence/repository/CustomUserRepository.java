package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.user.User;

import java.util.Set;

public interface CustomUserRepository {

    Set<User> findOpponents(UserRepositoryImpl.FindOpponentsParams params);

}
