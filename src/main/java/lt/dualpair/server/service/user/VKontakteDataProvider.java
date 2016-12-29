package lt.dualpair.server.service.user;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteDate;
import org.springframework.social.vkontakte.api.VKontakteProfile;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class VKontakteDataProvider implements SocialDataProvider {

    private static final Logger logger = Logger.getLogger(VKontakteDataProvider.class.getName());

    private Connection<? extends VKontakte> vkontakteConnection;

    public VKontakteDataProvider(Connection<? extends VKontakte> vkontakteConnection) {
        this.vkontakteConnection = vkontakteConnection;
    }

    @Override
    public String getAccountId() {
        return Long.toString(getUserId());
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

        user.setPhotos(vKontakte.mediaOperations().getProfilePhotos(vkUser.getId(), 5)
                .getItems()
                .stream()
                .map(vkPhoto -> {
                    Photo photo = new Photo();
                    photo.setUser(user);
                    photo.setSourceLink(vkPhoto.getPhoto604());
                    photo.setIdOnAccount(Long.toString(vkPhoto.getPhotoId()));
                    photo.setAccountType(UserAccount.Type.VKONTAKTE);
                    return photo;
                }).collect(Collectors.toList()));

        return user;
    }

    private User.Gender resolveGender(String gender) throws SocialDataException {
        if ("M".equals(gender)) {
            return User.Gender.MALE;
        } else if ("W".equals(gender)) {
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
        return vkontakteConnection.getApi().mediaOperations().getAll(getUserId(), 20)
                .getItems()
                .stream()
                .map(vkPhoto -> {
                    Photo photo = new Photo();
                    photo.setAccountType(UserAccount.Type.VKONTAKTE);
                    photo.setIdOnAccount(Long.toString(vkPhoto.getPhotoId()));
                    photo.setSourceLink(vkPhoto.getPhoto604());
                    return photo;
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<Photo> getPhoto(String idOnAccount) {
        Map<String, String> userAndPhotoIds = new HashMap<>();
        userAndPhotoIds.put(getAccountId(), idOnAccount);
        List<org.springframework.social.vkontakte.api.attachment.Photo> vkPhotos = vkontakteConnection.getApi().mediaOperations().getById(userAndPhotoIds);
        if (vkPhotos == null || vkPhotos.isEmpty()) {
            return Optional.empty();
        }
        org.springframework.social.vkontakte.api.attachment.Photo vkPhoto = vkPhotos.get(0);
        Photo photo = new Photo();
        photo.setIdOnAccount(idOnAccount);
        photo.setAccountType(UserAccount.Type.VKONTAKTE);
        photo.setSourceLink(vkPhoto.getPhoto604());
        return Optional.of(photo);
    }

    @Override
    public List<Photo> getPhotos(List<String> ids) {
        ConnectionData connectionData = vkontakteConnection.createData();
        UserActor userActor = new UserActor(Integer.valueOf(connectionData.getProviderUserId()), connectionData.getAccessToken());
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        List<Photo> photos = new ArrayList<>();
        try {
            List<String> userAndPhotoIds = ids.stream().map(photoId -> connectionData.getProviderUserId() + "_" + photoId).collect(Collectors.toList());
            List<com.vk.api.sdk.objects.photos.Photo> vkPhotos = vk.photos().getById(userActor, userAndPhotoIds).execute();
            for (com.vk.api.sdk.objects.photos.Photo vkPhoto : vkPhotos) {
                Photo photo = new Photo();
                photo.setIdOnAccount(Integer.toString(vkPhoto.getId()));
                photo.setAccountType(UserAccount.Type.VKONTAKTE);
                photo.setSourceLink(vkPhoto.getPhoto604());
                photos.add(photo);
            }
        } catch (ApiException | ClientException e) {
            logger.error(e.getMessage(), e);
        }
        return photos;
    }

    private Long getUserId() {
        return vkontakteConnection.getApi().usersOperations().getUser().getId();
    }
}
