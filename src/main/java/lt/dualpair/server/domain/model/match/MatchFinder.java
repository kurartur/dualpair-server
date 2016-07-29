package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.user.User;

public interface MatchFinder {

    Match findFor(User user, SearchParameters searchParameters);

}
