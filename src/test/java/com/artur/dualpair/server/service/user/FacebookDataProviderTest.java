package com.artur.dualpair.server.service.user;

import com.artur.dualpair.server.domain.model.user.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.UserOperations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FacebookDataProviderTest {

    private FacebookDataProvider facebookDataProvider;
    private Connection<Facebook> facebookConnection = mock(Connection.class);
    private Facebook facebook = mock(Facebook.class);
    private UserOperations userOperations = mock(UserOperations.class);
    private org.springframework.social.facebook.api.User facebookUser = mock(org.springframework.social.facebook.api.User.class);
    private UserProfile userProfile = new UserProfile("id", "name", "firstName", "lastName", "email", "username");

    @Before
    public void setUp() throws Exception {
        facebookDataProvider = new FacebookDataProvider(facebookConnection);
        when(facebookConnection.getApi()).thenReturn(facebook);
        when(facebookConnection.fetchUserProfile()).thenReturn(userProfile);
        when(facebook.userOperations()).thenReturn(userOperations);
        when(userOperations.getUserProfile()).thenReturn(facebookUser);
    }

    @Test
    public void testEnhanceUser() throws Exception {
        when(facebookUser.getFirstName()).thenReturn("firstName");
        LocalDate dateOfBirth = createFacebookUserDateOfBirth(false);
        when(facebookUser.getBirthday()).thenReturn(dateOfBirthToString(dateOfBirth, false));
        when(facebookUser.getGender()).thenReturn("male");

        User user = new User();
        user = facebookDataProvider.enhanceUser(user);
        assertEquals("firstName", user.getName());
        assertEquals("email", user.getEmail());
        assertEquals((Integer) 5, user.getAge());
        assertEquals(Date.from(dateOfBirth.atStartOfDay(ZoneId.systemDefault()).toInstant()), user.getDateOfBirth());
        assertEquals(User.Gender.MALE, user.getGender());
    }

    @Test
    public void testEnhanceUser_genderMale() throws Exception {
        when(facebookUser.getGender()).thenReturn("male");
        User user = new User();
        user = facebookDataProvider.enhanceUser(user);
        assertEquals(User.Gender.MALE, user.getGender());
    }

    @Test
    public void testEnhanceUser_genderFemale() throws Exception {
        when(facebookUser.getGender()).thenReturn("female");
        User user = new User();
        user = facebookDataProvider.enhanceUser(user);
        assertEquals(User.Gender.FEMALE, user.getGender());
    }

    @Test
    public void testEnhanceUser_invalidGender() throws Exception {
        when(facebookUser.getGender()).thenReturn("other");
        User user = new User();
        try {
            facebookDataProvider.enhanceUser(user);
            fail();
        } catch (SocialDataException sde) {
            assertEquals("Invalid gender 'other'", sde.getMessage());
        }
    }

    @Test
    public void testEnhanceUser_dateYearOnly() throws Exception {
        when(facebookUser.getGender()).thenReturn("male");
        LocalDate dateOfBirth = createFacebookUserDateOfBirth(true);
        when(facebookUser.getBirthday()).thenReturn(dateOfBirthToString(dateOfBirth, true));

        User user = new User();
        facebookDataProvider.enhanceUser(user);
        assertEquals((Integer) 5, user.getAge());
    }

    private LocalDate createFacebookUserDateOfBirth(boolean yearOnly) {
        LocalDate date = LocalDate.now().minus(5, ChronoUnit.YEARS);
        if (yearOnly) {
            date.withMonth(1).withDayOfMonth(1);
        } else {
            date.minus(1, ChronoUnit.DAYS);
        }
        return date;
    }

    private String dateOfBirthToString(LocalDate dateOfBirth, boolean yearOnly) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(yearOnly ? "yyyy" : "MM/dd/yyyy");
        return dateOfBirth.format(formatter);
    }
}