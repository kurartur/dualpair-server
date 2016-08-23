package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.Validate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class.getName());

    protected UserRepository userRepository;
    private SociotypeRepository sociotypeRepository;
    protected UserDetailsService userDetailsService;

    @Override
    public User loadUserById(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        return user.get();
    }



    private User updateUser(User user) {
        user.setUpdated(new Date());
        userRepository.save(user);
        return user;
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
    public void setUserSociotypes(Long userId, Set<Sociotype.Code1> codes) {
        Validate.notNull(userId, "User id is mandatory");
        Validate.notNull(codes, "Sociotype codes are mandatory");
        if (codes.size() < 1 || codes.size() > 2) {
            throw new IllegalArgumentException("Invalid sociotype code count. Must be 1 or 2");
        }

        User user = loadUserById(userId);
        Set<Sociotype> sociotypes = sociotypeRepository.findByCode1List(new ArrayList<>(codes));
        if (sociotypes.isEmpty()) {
            throw new IllegalStateException("Zero sociotypes found");
        }
        user.setSociotypes(sociotypes);
        updateUser(user);
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
}
