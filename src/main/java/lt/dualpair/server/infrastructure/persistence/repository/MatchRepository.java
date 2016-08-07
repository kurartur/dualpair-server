package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchParty;
import lt.dualpair.server.domain.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MatchRepository extends CrudRepository<Match, Long> {

    @Query("select mp.match from MatchParty mp where mp.match.id = ?1 and mp.user.id = ?2")
    Optional<Match> findOneByUser(Long matchId, Long userId);

    @Query("select mp.match from MatchParty mp where mp.user = ?1 and mp.response = ?2")
    Set<Match> findByUser(User user, MatchParty.Response response);

    @Query("" +
            "select m from Match m, MatchParty mp1, MatchParty mp2 " +
            "where m = mp1.match and mp1.user = ?1 " +
            "   and m = mp2.match and mp2.user != ?1 and mp2.user.id not in ?2 " +
            "   and (mp2.response = 0 or mp2.response = 2) and mp1.response = 0 ")
    Set<Match> findNotReviewed(User user, List<Long> excludeOpponents);

}
