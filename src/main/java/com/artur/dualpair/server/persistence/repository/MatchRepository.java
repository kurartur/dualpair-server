package com.artur.dualpair.server.persistence.repository;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface MatchRepository extends CrudRepository<Match, Long> {

    @Query("select m from Match m where m.opponent = ?1")
    Set<Match> findByOpponent(User opponent);

}
