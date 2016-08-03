package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.user.User;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

public class MatchRequestBuilder {

    private MatchRequest matchRequest = new MatchRequest();

    MatchRequestBuilder(User user) {
        matchRequest.setUser(user);
    }

    public static MatchRequestBuilder findFor(User user) {
        return new MatchRequestBuilder(user);
    }

    public MatchRequestBuilder ageRange(int minAge, int maxAge) {
        if (minAge < 0 || maxAge < 0) {
            throw new IllegalArgumentException("Age can't be negative");
        }
        if (minAge > maxAge) {
            throw  new IllegalArgumentException("Min age can't be higher than max age");
        }
        matchRequest.setMinAge(minAge);
        matchRequest.setMaxAge(maxAge);
        return this;
    }

    public MatchRequestBuilder genders(Set<User.Gender> genders) {
        Assert.notEmpty(genders);
        Assert.noNullElements(genders.toArray());
        matchRequest.setGenders(genders);
        return this;
    }

    public MatchRequestBuilder location(double latitude, double longitude, String countryCode) {
        Assert.notNull(countryCode);
        matchRequest.setLatitude(latitude);
        matchRequest.setLongitude(longitude);
        matchRequest.setCountryCode(countryCode);
        return this;
    }

    public MatchRequestBuilder excludeOpponents(List<Long> opponentIds) {
        Assert.notEmpty(opponentIds);
        Assert.noNullElements(opponentIds.toArray());
        matchRequest.setExcludedOpponentIds(opponentIds);
        return this;
    }

    public MatchRequestBuilder apply(SearchParameters searchParameters) {
        Assert.notNull(searchParameters);
        Assert.notNull(searchParameters.getLocation());
        ageRange(searchParameters.getMinAge(), searchParameters.getMaxAge());
        genders(searchParameters.getSearchGenders());
        Location location = searchParameters.getLocation();
        location(location.getLatitude(), location.getLongitude(), location.getCountryCode());
        return this;
    }

    public MatchRequest build() {
        return matchRequest;
    }

}
