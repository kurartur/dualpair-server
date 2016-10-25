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

    void updateUser(User user);

    UserLocation addLocation(Long userId, Location location);

    void setUserSociotypes(User user, Set<Sociotype> sociotypes);

    void setUserDateOfBirth(Long userId, Date date);

    void setUserSearchParameters(Long userId, SearchParameters sp);

    void deleteUserPhoto(Long userId, Long photoId);
}
