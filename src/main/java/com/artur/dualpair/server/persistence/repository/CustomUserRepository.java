package com.artur.dualpair.server.persistence.repository;

import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;

import java.util.Set;

public interface CustomUserRepository {

    Set<User> findOpponent(User user, Sociotype sociotype, SearchParameters searchParameters);

}
