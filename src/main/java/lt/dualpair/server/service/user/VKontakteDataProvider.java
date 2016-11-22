package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteDate;
import org.springframework.social.vkontakte.api.VKontakteProfile;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class VKontakteDataProvider implements SocialDataProvider {

    private static final Logger logger = Logger.getLogger(VKontakteDataProvider.class.getName());

    private Connection<? extends VKontakte> vkontakteConnection;

    public VKontakteDataProvider(Connection<? extends VKontakte> vkontakteConnection) {
        this.vkontakteConnection = vkontakteConnection;
    }

    @Override
    public String getAccountId() {
        return Long.toString(vkontakteConnection.getApi().usersOperations().getUser().getId());
    }

    @Override
    public User enhanceUser(User user) throws SocialDataException {
        VKontakte vKontakte = vkontakteConnection.getApi();
        VKontakteProfile vkUser = vKontakte.usersOperations().getUser("sex, bdate");
        if (!StringUtils.isEmpty(vkUser.getFirstName())) {
            user.setName(vkUser.getFirstName());
        }
        user.setEmail(vkontakteConnection.fetchUserProfile().getEmail());
        user.setGender(resolveGender(vkUser.getGender()));

        VKontakteDate birthDate = vkUser.getBirthDate();
        if (!StringUtils.isEmpty(birthDate)) {
            Date dateOfBirth = vkBirthDateToDate(birthDate);
            if (dateOfBirth != null) {
                user.setDateOfBirth(dateOfBirth);
            }
        }

        //addPhotos(user, vKontakte);

        return user;
    }

    private User.Gender resolveGender(String gender) throws SocialDataException {
        if ("2".equals(gender)) {
            return User.Gender.MALE;
        } else if ("1".equals(gender)) {
            return User.Gender.FEMALE;
        } else {
            logger.error("Invalid gender " + gender);
            throw new SocialDataException("Invalid gender '" + gender +"'");
        }
    }

    private Date vkBirthDateToDate(VKontakteDate birthDate) {
        if (birthDate.getYear() != 0) {
            LocalDate localDate = LocalDate.of(birthDate.getYear(), birthDate.getMonth(), birthDate.getDay());
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    @Override
    public List<Photo> getPhotos() {
        return new ArrayList<>();
    }

    @Override
    public Optional<Photo> getPhoto(String idOnAccount) {
        return null;
    }
}
