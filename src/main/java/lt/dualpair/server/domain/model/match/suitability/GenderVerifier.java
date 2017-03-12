package lt.dualpair.server.domain.model.match.suitability;

import lt.dualpair.server.domain.model.user.Gender;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class GenderVerifier {

    public boolean verify(Gender gender, Set<Gender> genders) {
        return genders.contains(gender);
    }

}
