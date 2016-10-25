package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.socionics.RelationType;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;
import lt.dualpair.server.infrastructure.persistence.repository.MatchRepository;
import lt.dualpair.server.infrastructure.persistence.repository.PhotoRepository;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class.getName());

    protected UserRepository userRepository;
    private SociotypeRepository sociotypeRepository;
    protected UserDetailsService userDetailsService;
    private MatchRepository matchRepository;
    private PhotoRepository photoRepository;

    @Override
    public User loadUserById(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        return user.get();
    }

    @Override
    public void updateUser(User user) {
        user.setDateUpdated(new Date());
        userRepository.save(user);
    }

    protected String buildUserId(String accountId, Long time) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(accountId.getBytes("UTF-8"));
            outputStream.write(time.toString().getBytes("UTF-8"));

            byte[] md5 = messageDigest.digest(outputStream.toByteArray());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < md5.length; ++i)
                sb.append(Integer.toHexString((md5[i] & 0xFF) | 0x100).substring(1,3));

            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("Was not able to generate user id: " + e.getMessage(), e);
            return time.toString();
        }
    }

    @Override
    @Transactional
    public void setUserSociotypes(User user, Set<Sociotype> sociotypes) {
        Assert.notNull(user, "User is mandatory");
        Assert.notNull(sociotypes, "Sociotypes are mandatory");

        boolean changed = !user.getSociotypes().containsAll(sociotypes);

        user.setSociotypes(sociotypes);
        updateUser(user);

        if (changed) {
            removeInvalidMatches(user, sociotypes.stream()
                    .map(sociotype -> sociotypeRepository.findOppositeByRelationType(sociotype.getCode1(), RelationType.Code.DUAL))
                    .collect(Collectors.toSet()));
        }
    }

    private void removeInvalidMatches(final User user, final Set<Sociotype> validSociotypes) {
        matchRepository.findForPossibleRemoval(user).forEach(match -> {
            if (Collections.disjoint(match.getOppositeMatchParty(user.getId()).getUser().getSociotypes(), validSociotypes)) {
                matchRepository.delete(match);
            }
        });
    }

    @Override
    public void setUserDateOfBirth(Long userId, Date date) {
        User user = loadUserById(userId);
        user.setDateOfBirth(date);
        updateUser(user);
    }

    @Override
    public void setUserSearchParameters(Long userId, SearchParameters sp) {
        User user = loadUserById(userId);
        SearchParameters current = user.getSearchParameters();
        if (current == null) {
            user.setSearchParameters(sp);
            sp.setUser(user);
        } else {
            current.setFrom(sp);
        }
        updateUser(user);
    }

    @Override
    @Transactional
    public UserLocation addLocation(Long userId, Location location) {
        User user = loadUserById(userId);
        UserLocation userLocation = new UserLocation(user, location.getLatitude(), location.getLongitude(), location.getCountryCode(), location.getCity());
        user.addLocation(userLocation);
        userRepository.save(user);
        return userLocation;
    }

    @Override
    @Transactional
    public void deleteUserPhoto(Long userId, Long photoId) {
        User user = userRepository.findById(userId).get();
        if (user.getPhotos().size() < 2) {
            throw new IllegalStateException("User must have at least one photo");
        }
        photoRepository.delete(photoId);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSociotypeRepository(SociotypeRepository sociotypeRepository) {
        this.sociotypeRepository = sociotypeRepository;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setMatchRepository(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Autowired
    public void setPhotoRepository(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }
}
