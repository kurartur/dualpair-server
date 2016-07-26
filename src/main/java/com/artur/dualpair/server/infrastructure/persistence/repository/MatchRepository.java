package com.artur.dualpair.server.infrastructure.persistence.repository;

import com.artur.dualpair.server.domain.model.match.Match;
import com.artur.dualpair.server.domain.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface MatchRepository extends CrudRepository<Match, Long> {

    @Query("select m from Match m where m.opponent = ?1")
    Set<Match> findByOpponent(User opponent);

    @Query("select m from Match m where m.user = ?1")
    Set<Match> findByUser(User user);

}
