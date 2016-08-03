package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.user.User;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatchRequestBuilderTest {

    @Test
    public void testAgeRange() throws Exception {
        MatchRequest mr = new MatchRequestBuilder(null).ageRange(10, 20).build();
        assertEquals(10, mr.getMinAge());
        assertEquals(20, mr.getMaxAge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAgeRange_invalidMinAge() throws Exception {
        new MatchRequestBuilder(null).ageRange(-1, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAgeRange_invalidMaxAge() throws Exception {
        new MatchRequestBuilder(null).ageRange(10, -1);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAgeRange_invalidAges() throws Exception {
        new MatchRequestBuilder(null).ageRange(20, 10);
    }

    @Test
    public void testGenders() throws Exception {
        MatchRequest mr = new MatchRequestBuilder(null).genders(new HashSet<>(Arrays.asList(User.Gender.FEMALE, User.Gender.MALE))).build();
        assertTrue(mr.getGenders().contains(User.Gender.FEMALE));
        assertTrue(mr.getGenders().contains(User.Gender.MALE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenders_null() throws Exception {
        new MatchRequestBuilder(null).genders(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenders_empty() throws Exception {
        new MatchRequestBuilder(null).genders(new HashSet<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenders_nullElements() throws Exception {
        Set<User.Gender> genders = new HashSet<>();
        genders.add(null);
        new MatchRequestBuilder(null).genders(genders);
    }

    @Test
    public void testLocation() throws Exception {
        MatchRequest mr = new MatchRequestBuilder(null).location(10.0, 11.0, "LT").build();
        assertEquals(10.0, mr.getLatitude(), 0);
        assertEquals(11.0, mr.getLongitude(), 0);
        assertEquals("LT", mr.getCountryCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocation_nullCountry() throws Exception {
        new MatchRequestBuilder(null).location(10.0, 11.0, null);
    }

    @Test
    public void testExcludeOpponents() throws Exception {
        MatchRequest mr = new MatchRequestBuilder(null).excludeOpponents(Arrays.asList(10L, 20L)).build();
        assertEquals(2, mr.getExcludedOpponentIds().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExcludeOpponents_empty() throws Exception {
        new MatchRequestBuilder(null).excludeOpponents(new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExcludeOpponents_null() throws Exception {
        new MatchRequestBuilder(null).excludeOpponents(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExcludeOpponents_nullElements() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(null);
        new MatchRequestBuilder(null).excludeOpponents(ids);
    }

    @Test
    public void testApplySearchParameters() throws Exception {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(10);
        searchParameters.setMaxAge(20);
        searchParameters.setSearchMale(true);
        searchParameters.setSearchFemale(true);
        searchParameters.setLocation(new Location(10.0, 11.0, "LT", "City"));
        MatchRequest mr = new MatchRequestBuilder(null).apply(searchParameters).build();
        assertEquals(10, mr.getMinAge());
        assertEquals(20, mr.getMaxAge());
        assertTrue(mr.getGenders().contains(User.Gender.FEMALE));
        assertTrue(mr.getGenders().contains(User.Gender.MALE));
        assertEquals(10.0, mr.getLatitude(), 0);
        assertEquals(11.0, mr.getLongitude(), 0);
        assertEquals("LT", mr.getCountryCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApplySearchParameters_null() throws Exception {
        new MatchRequestBuilder(null).apply(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApplySearchParameters_nullLocation() throws Exception {
        SearchParameters searchParameters = new SearchParameters();
        new MatchRequestBuilder(null).apply(searchParameters);
    }
}