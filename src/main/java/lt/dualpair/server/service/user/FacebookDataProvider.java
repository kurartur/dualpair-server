package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.MediaOperations;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        //user.setLocation(facebookUser.getLocation().getName());
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

    @Override
    public List<Photo> getPhotos() {
        Facebook facebook = facebookConnection.getApi();
        MediaOperations mediaOperations = facebook.mediaOperations();
        List<Photo> photos = new ArrayList<>();
        mediaOperations.getAlbums().forEach(album -> mediaOperations.getPhotos(album.getId()).forEach(fbPhoto -> {
            Photo photo = new Photo();
            photo.setSourceLink(fbPhoto.getSource());
            photo.setAccountType(UserAccount.Type.FACEBOOK);
            photo.setIdOnAccount(fbPhoto.getId());
            photos.add(photo);
        }));
        return photos;
    }

    @Override
    public Optional<Photo> getPhoto(String photoId) {
        try {
            org.springframework.social.facebook.api.Photo fbPhoto = facebookConnection.getApi().mediaOperations().getPhoto(photoId);
            if (fbPhoto != null) {
                Photo photo = new Photo();
                photo.setIdOnAccount(fbPhoto.getId());
                photo.setAccountType(UserAccount.Type.FACEBOOK);
                photo.setSourceLink(fbPhoto.getSource());
                return Optional.of(photo);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private User.Gender resolveGender(String gender) throws SocialDataException {
        if ("male".equals(gender)) {
            return User.Gender.MALE;
        } else if ("female".equals(gender)) {
            return User.Gender.FEMALE;
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
                for (org.springframework.social.facebook.api.Photo photo : facebook.mediaOperations().getPhotos(profilePictureAlbum.getId())) {
                    user.getPhotos().add(buildPhoto(photo, user));
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
        photo.setAccountType(UserAccount.Type.FACEBOOK);
        photo.setIdOnAccount(facebookPhoto.getId());
        photo.setSourceLink(facebookPhoto.getSource());
        return photo;
    }
}
