package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;

import java.util.Set;

public interface CustomUserRepository {

    Set<User> findOpponent(User user, Sociotype sociotype, SearchParameters searchParameters);

}
