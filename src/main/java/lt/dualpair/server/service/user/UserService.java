package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;

import java.util.Date;
import java.util.Set;

public interface UserService {

    User loadUserById(Long userId) throws UserNotFoundException;

    UserLocation addLocation(Long userId, Location location);

    void setUserSociotypes(Long userId, Set<Sociotype.Code1> codes);

    void setUserDateOfBirth(Long userId, Date date);

    void setUserSearchParameters(Long userId, SearchParameters sp);

}
