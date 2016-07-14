package com.artur.dualpair.server.service.user;

import com.artur.dualpair.server.domain.model.user.User;
import org.jboss.logging.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Photo;
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
        user.setName(facebookUser.getFirstName());
        user.setEmail(facebookConnection.fetchUserProfile().getEmail());
        //user.setLocation(facebookUser.getLocation().getName());
        user.setGender(resolveGender(facebookUser.getGender()));

        user.setDateOfBirth(null);
        String birthday = facebookUser.getBirthday();
        if (!StringUtils.isEmpty(birthday)) {
            Date dateOfBirth = birthdayToDate(birthday);
            if (dateOfBirth != null) {
                user.setDateOfBirth(dateOfBirth);
            }
        }
        //user.setProfilePictureLinks(getProfilePictureLinks(facebook));
        return user;
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

    private Set<String> getProfilePictureLinks(Facebook facebook) {
        Set<String> link = new LinkedHashSet<>();
        String albumId = getProfilePictureAlbum(facebook.mediaOperations().getAlbums()).getId();
        for (Photo photo : facebook.mediaOperations().getPhotos(albumId)) {
            link.add(photo.getSource());
        }
        return link;
    }

    private Album getProfilePictureAlbum(List<Album> albums) {
        for (Album album : albums) {
            if (album.getName().startsWith("Profile ")) {
                return album;
            }
        }
        throw new RuntimeException("No profile pics found"); // TODO
    }
}
