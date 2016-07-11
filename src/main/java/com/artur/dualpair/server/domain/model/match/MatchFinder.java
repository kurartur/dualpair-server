package com.artur.dualpair.server.domain.model.match;

import com.artur.dualpair.server.domain.model.Match;
import com.artur.dualpair.server.domain.model.user.User;

public interface MatchFinder {

    Match findFor(User user, SearchParameters searchParameters);

}
