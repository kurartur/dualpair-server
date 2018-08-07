package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.MediaOperations;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import java.net.URI;
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
            photos.add(photo);
        }));
        return photos;
    }

    @Override
    public Optional<Photo> getPhoto(String photoId) {
        try {
            RestOperations restOperations = facebookConnection.getApi().restOperations();
            URI uri = URIBuilder.fromUri("https://graph.facebook.com/v2.5/" + photoId + "?fields=source").build();
            org.springframework.social.facebook.api.Photo fbPhoto = restOperations.getForObject(uri, org.springframework.social.facebook.api.Photo.class);
            if (fbPhoto != null) {
                Photo photo = new Photo();
                photo.setSourceLink(fbPhoto.getSource());
                return Optional.of(photo);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.warn(e);
            return Optional.empty();
        }
    }

    @Override
    public List<Photo> getPhotos(List<String> ids) {
        List<Photo> photos = new ArrayList<>();
        for (String idOnAccount : ids) {
            getPhoto(idOnAccount).ifPresent(photo -> photos.add(photo));
        }
        return photos;
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
