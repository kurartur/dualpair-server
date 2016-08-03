package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MatchRequestValidatorTest {

    private MatchRequestValidator validator = new MatchRequestValidator();

    @Test
    public void testValidateMatchRequest() throws Exception {
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        user.setSearchParameters(searchParameters);

        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("User must provide at least one sociotype", mre.getMessage());
        }

        Set<Sociotype> sociotypes = new HashSet<>();
        sociotypes.add(new Sociotype.Builder().code2(Sociotype.Code2.ENTJ).build());
        user.setSociotypes(sociotypes);
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("User must provide age", mre.getMessage());
        }

        user.setDateOfBirth(new Date());
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("User must provide gender", mre.getMessage());
        }

        user.setGender(User.Gender.MALE);
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Invalid search parameters: min age is missing", mre.getMessage());
        }

        searchParameters.setMinAge(20);
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Invalid search parameters: max age is missing", mre.getMessage());
        }

        searchParameters.setMaxAge(25);
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Invalid search parameters: male/female criteria is missing", mre.getMessage());
        }

        searchParameters.setSearchMale(false);
        searchParameters.setSearchFemale(true);
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Invalid search parameters: location is missing", mre.getMessage());
        }

        searchParameters.setLocation(new Location(0.0, 0.0, "", "city"));
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Invalid search parameters: location is missing", mre.getMessage());
        }

        searchParameters.setLocation(new Location(0.0, 0.0, "LT", "city"));
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Invalid search parameters: location is missing", mre.getMessage());
        }

        searchParameters.setLocation(new Location(1.0, 0.0, "LT", "city"));
        try {
            validator.validateMatchRequest(user, searchParameters);
            fail();
        } catch (MatchRequestException mre) {
            assertEquals("Invalid search parameters: location is missing", mre.getMessage());
        }

        searchParameters.setLocation(new Location(1.0, 1.0, "LT", "city"));
        validator.validateMatchRequest(user, searchParameters);
    }
}