package lt.dualpair.server.domain.model.user;

import lt.dualpair.server.domain.model.socionics.Sociotype;

import java.util.Arrays;
import java.util.HashSet;

public class UserTestUtils {

    public static User createUser() {
        return new User();
    }

    public static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public static User createUser(Long id, Sociotype.Code1 sociotypeCode) {
        User user = new User();
        user.setId(id);
        Sociotype sociotype = new Sociotype.Builder().code1(sociotypeCode).build();
        user.setSociotypes(new HashSet<>(Arrays.asList(sociotype)));
        return user;
    }

    public static User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

}