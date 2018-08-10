package lt.dualpair.server.service.user;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.base.Sex;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class VKontakteDataProvider implements SocialDataProvider {

    private static final Logger logger = Logger.getLogger(VKontakteDataProvider.class.getName());

    private VkApiClient vkApiClient = createVpApiClient();
    private UserActor userActor;

    public VKontakteDataProvider(Connection<? extends VKontakte> vkontakteConnection) {
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
        try {
            List<UserXtrCounters> list = vkApiClient.users()
                    .get(userActor)
                    .fields(UserField.SEX, UserField.BDATE)
                    .execute();
            if (!list.isEmpty()) {
                UserXtrCounters userXtrCounters = list.get(0);
                if (!StringUtils.isEmpty(userXtrCounters.getFirstName())) {
                    user.setName(userXtrCounters.getFirstName());
                }
                // TODO email user.setEmail(null);
                user.setGender(resolveGender(userXtrCounters.getSex()));
                String birthDate = userXtrCounters.getBdate();
                if (!StringUtils.isEmpty(birthDate)) {
                    Date dateOfBirth = vkBirthDateToDate(birthDate);
                    if (dateOfBirth != null) {
                        user.setDateOfBirth(dateOfBirth);
                    }
                }
            }
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

    private Gender resolveGender(Sex gender) {
        if (gender == Sex.MALE) {
            return Gender.MALE;
        } else if (gender == Sex.FEMALE) {
            return Gender.FEMALE;
        }
        return null;
    }

    private Date vkBirthDateToDate(String birthDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy", Locale.ENGLISH);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat.parse(birthDate);
        } catch (ParseException e) {
            logger.error(e);
            return null;
        }
    }

}
