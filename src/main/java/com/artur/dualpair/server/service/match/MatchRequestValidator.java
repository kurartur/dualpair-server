package com.artur.dualpair.server.service.match;

import com.artur.dualpair.server.domain.model.geo.Location;
import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.user.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.Validate;

@Component
public class MatchRequestValidator {

    public void validateMatchRequest(User user, SearchParameters searchParameters) throws MatchRequestException {
        Validate.notNull(user, "User not provided");
        Validate.notNull(searchParameters, "Search parameters are not provided");
        if (user.getSociotypes().isEmpty()) {
            throw new MatchRequestException("User must provide at least one sociotype");
        }
        if (user.getAge() == null) {
            throw new MatchRequestException("User must provide age");
        }
        if (user.getGender() == null) {
            throw new MatchRequestException("User must provide gender");
        }
        if (searchParameters.getMinAge() == 0) {
            throw new MatchRequestException("Invalid search parameters: min age is missing");
        }
        if (searchParameters.getMaxAge() == 0) {
            throw new MatchRequestException("Invalid search parameters: max age is missing");
        }
        if (!searchParameters.getSearchMale() && !searchParameters.getSearchFemale()) {
            throw new MatchRequestException("Invalid search parameters: male/female criteria is missing");
        }
        Location location = searchParameters.getLocation();
        if (location == null
                || StringUtils.isEmpty(location.getCountryCode())
                || location.getLatitude() == 0
                || location.getLongitude() == 0) {
            throw new MatchRequestException("Invalid search parameters: location is missing");
        }
    }

}
