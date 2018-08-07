package lt.dualpair.server.service.user;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteDate;
import org.springframework.social.vkontakte.api.VKontakteProfile;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

public class VKontakteDataProvider implements SocialDataProvider {

    private static final Logger logger = Logger.getLogger(VKontakteDataProvider.class.getName());

    private Connection<? extends VKontakte> vkontakteConnection;

    private VkApiClient vkApiClient = createVpApiClient();

    private UserActor userActor;

    public VKontakteDataProvider(Connection<? extends VKontakte> vkontakteConnection) {
        this.vkontakteConnection = vkontakteConnection;
        ConnectionData connectionData = vkontakteConnection.createData();
        userActor = new UserActor(Integer.valueOf(connectionData.getProviderUserId()), connectionData.getAccessToken());
    }

    protected VkApiClient createVpApiClient() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient);
    }

    @Override
    public String getAccountId() {
        return userActor.getId().toString();
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

        try {
            user.setPhotos(vkApiClient.photos().get(userActor)
                        .albumId("profile")
                        .count(User.MAX_NUMBER_OF_PHOTOS)
                        .rev(true)
                        .execute().getItems()
                    .stream()
                    .map(vkPhoto -> {
                        Photo photo = new Photo();
                        photo.setUser(user);
                        photo.setSourceLink(vkPhoto.getPhoto604());
                        return photo;
                    }).collect(Collectors.toList()));
        } catch (ApiException | ClientException e) {
            logger.error(e.getMessage(), e);
            throw new SocialDataException(e.getMessage(), e);
        }

        return user;
    }

    private Gender resolveGender(String gender) throws SocialDataException {
        if ("M".equals(gender)) {
            return Gender.MALE;
        } else if ("W".equals(gender)) {
            return Gender.FEMALE;
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

}
