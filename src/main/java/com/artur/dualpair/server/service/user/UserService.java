package com.artur.dualpair.server.service.user;

import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import com.artur.dualpair.server.persistence.repository.SociotypeRepository;
import com.artur.dualpair.server.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    protected UserRepository userRepository;
    private SociotypeRepository sociotypeRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    public User loadUserByUserId(String userId) throws UsernameNotFoundException {
        return findByUsername(userId);
    }

    private User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
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

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md5.length; ++i)
                sb.append(Integer.toHexString((md5[i] & 0xFF) | 0x100).substring(1,3));

            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("Was not able to generate user id: " + e.getMessage(), e);
            return time.toString();
        }
    }

    public void setUserSociotypes(String userId, Set<Sociotype.Code1> codes) {
        Validate.notNull(userId, "User id is mandatory");
        Validate.notNull(codes, "Sociotype codes are mandatory");
        if (codes.size() < 1 || codes.size() > 2) {
            throw new IllegalArgumentException("Invalid sociotype code count. Must be 1 or 2");
        }

        User user = findByUsername(userId);
        Set<Sociotype> sociotypes = sociotypeRepository.findByCode1List(new ArrayList<>(codes));
        if (sociotypes.isEmpty()) {
            throw new IllegalStateException("Zero sociotypes found");
        }
        user.setSociotypes(sociotypes);
        updateUser(user);
    }

    public void setUserDateOfBirth(String userId, Date date) {
        User user = loadUserByUserId(userId);
        user.setDateOfBirth(date);
        updateUser(user);
    }

    public void setUserSearchParameters(String username, SearchParameters sp) {
        User user = loadUserByUsername(username);
        SearchParameters current = user.getSearchParameters();
        if (current == null) {
            user.setSearchParameters(sp);
            sp.setUser(user);
        } else {
            current.setSearchMale(sp.getSearchMale());
            current.setSearchFemale(sp.getSearchFemale());
            current.setMinAge(sp.getMinAge());
            current.setMaxAge(sp.getMaxAge());
            current.setLocation(sp.getLocation());
        }
        updateUser(user);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSociotypeRepository(SociotypeRepository sociotypeRepository) {
        this.sociotypeRepository = sociotypeRepository;
    }
}
