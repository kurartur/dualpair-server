package lt.dualpair.server.domain.model.user;

import lt.dualpair.server.domain.model.socionics.Sociotype;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UserTestUtils {

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setDateOfBirth(Date.from(LocalDate.now().minusYears(5).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Set<Sociotype> sociotypes = new HashSet<>();
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.IEE).build());
        user.setSociotypes(sociotypes);

        user.setGender(User.Gender.MALE);
        user.addLocation(new UserLocation(user, 10.0, 11.0, "LT", "Vilnius"));

        return user;
    }

    public static User createUser(Long id) {
        User user = createUser();
        user.setId(id);
        return user;
    }

    public static User createUser(Long id, Sociotype.Code1 sociotypeCode) {
        User user = createUser(id);
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