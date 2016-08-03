package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.user.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchRequest {

    private static final int DEFAULT_RADIUS = 300000; // meters

    private User user;

    private int minAge;
    private int maxAge;

    private Set<User.Gender> genders = new HashSet<>();

    private double latitude;
    private double longitude;
    private String countryCode;
    private int radius = DEFAULT_RADIUS;

    private List<Long> excludedOpponentIds = new ArrayList<>();

    public List<Long> getExcludedOpponentIds() {
        return excludedOpponentIds;
    }

    public void setExcludedOpponentIds(List<Long> excludedOpponentIds) {
        this.excludedOpponentIds = excludedOpponentIds;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public Set<User.Gender> getGenders() {
        return genders;
    }

    public void setGenders(Set<User.Gender> genders) {
        this.genders = genders;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
