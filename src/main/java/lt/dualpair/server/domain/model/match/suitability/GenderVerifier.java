package lt.dualpair.server.domain.model.match.suitability;

import lt.dualpair.server.domain.model.user.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class GenderVerifier {

    public boolean verify(User.Gender gender, Set<User.Gender> genders) {
        return genders.contains(gender);
    }

}
