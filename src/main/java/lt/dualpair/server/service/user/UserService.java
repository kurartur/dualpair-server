package lt.dualpair.server.service.user;

import lt.dualpair.core.location.Location;
import lt.dualpair.core.socionics.Sociotype;
import lt.dualpair.core.user.SearchParameters;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserLocation;

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
