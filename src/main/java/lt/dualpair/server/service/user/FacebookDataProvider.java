package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FacebookDataProvider implements SocialDataProvider {

    private static final Logger logger = Logger.getLogger(FacebookDataProvider.class.getName());

    private Connection<? extends Facebook> facebookConnection;

    public FacebookDataProvider(Connection<? extends Facebook> facebookConnection) {
        this.facebookConnection = facebookConnection;
    }

    @Override
    public String getAccountId() {
        return facebookConnection.getApi().userOperations().getUserProfile().getId();
    }

    @Override
    public User enhanceUser(User user) throws SocialDataException {
        Facebook facebook = facebookConnection.getApi();
        org.springframework.social.facebook.api.User facebookUser = facebook.userOperations().getUserProfile();
        if (!StringUtils.isEmpty(facebookUser.getFirstName())) {
            user.setName(facebookUser.getFirstName());
        }
        user.setEmail(facebookConnection.fetchUserProfile().getEmail());
        user.setGender(resolveGender(facebookUser.getGender()));

        String birthday = facebookUser.getBirthday();
        if (!StringUtils.isEmpty(birthday)) {
            Date dateOfBirth = birthdayToDate(birthday);
            if (dateOfBirth != null) {
                user.setDateOfBirth(dateOfBirth);
            }
        }

        addPhotos(user, facebook);

        return user;
    }

    private Gender resolveGender(String gender) throws SocialDataException {
        if ("male".equals(gender)) {
            return Gender.MALE;
        } else if ("female".equals(gender)) {
            return Gender.FEMALE;
        } else {
            logger.error("Invalid gender " + gender);
            throw new SocialDataException("Invalid gender '" + gender +"'");
        }
    }

    private Date birthdayToDate(String birthday) {
        try {
            if (birthday.length() == 4) {
                return new SimpleDateFormat("yyyy", Locale.ENGLISH).parse(birthday);
            } else if (birthday.length() == 10) {
                return new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(birthday);
            }
            return null;
        } catch (ParseException pe) {
            return null;
        }
    }

    private void addPhotos(User user, Facebook facebook) {
        PagedList<Album> albums = facebook.mediaOperations().getAlbums();
        if (albums != null) {
            Album profilePictureAlbum = getProfilePictureAlbum(albums);
            if (profilePictureAlbum != null) {
                int c = 0;
                for (org.springframework.social.facebook.api.Photo photo : facebook.mediaOperations().getPhotos(profilePictureAlbum.getId())) {
                    user.addPhoto(buildPhoto(photo, user));
                    if (++c >= User.MAX_NUMBER_OF_PHOTOS) {
                        break;
                    }
                }
            }
        }
    }

    private Album getProfilePictureAlbum(List<Album> albums) {
        for (Album album : albums) {
            if (album.getName().startsWith("Profile ")) {
                return album;
            }
        }
        return null;
    }

    private Photo buildPhoto(org.springframework.social.facebook.api.Photo facebookPhoto, User user) {
        Photo photo = new Photo();
        photo.setUser(user);
        photo.setSourceLink(facebookPhoto.getSource());
        return photo;
    }
}
