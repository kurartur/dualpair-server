package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.core.match.Match;
import lt.dualpair.core.match.MatchRequestBuilder;
import lt.dualpair.core.match.MatchRequestException;
import lt.dualpair.core.match.UserAwareMatch;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.service.match.MatchService;
import lt.dualpair.server.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MatchController {

    private MatchService matchService;
    private MatchResourceAssembler matchResourceAssembler;
    private UserService userService;

    @Inject
    public MatchController(MatchService matchService, MatchResourceAssembler matchResourceAssembler, UserService userService) {
        this.matchService = matchService;
        this.matchResourceAssembler = matchResourceAssembler;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/match/next")
    public ResponseEntity next(@Valid SearchQuery searchQuery, @ActiveUser UserDetails principal) throws MatchRequestException {

        User freshUser = userService.loadUserById(new Long(principal.getUsername()));

        MatchRequestBuilder builder = MatchRequestBuilder.findFor(freshUser)
                .ageRange(searchQuery.getMinAge(), searchQuery.getMaxAge())
                .genders(searchQuery.getGenders());

        if (searchQuery.getExcludeOpponents() != null && !searchQuery.getExcludeOpponents().isEmpty()) {
            builder.excludeOpponents(searchQuery.getExcludeOpponents());
        }

        Match match = matchService.nextFor(builder.build());
        if (match == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(matchResourceAssembler.toResource(new UserAwareMatch(freshUser, match)));
    }

    public static class SearchQuery {

        @NotNull
        private Integer minAge;

        @NotNull
        private Integer maxAge;

        @NotNull
        private String searchFemale;

        @NotNull
        private String searchMale;

        private List<Long> excludeOpponents;

        public Integer getMinAge() {
            return minAge;
        }

        public void setMinAge(Integer minAge) {
            this.minAge = minAge;
        }

        public void setMia(Integer minAge) {
            this.minAge = minAge;
        }

        public Integer getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(Integer maxAge) {
            this.maxAge = maxAge;
        }

        public void setMaa(Integer maxAge) {
            this.maxAge = maxAge;
        }

        public String getSearchFemale() {
            return searchFemale;
        }

        public void setSearchFemale(String searchFemale) {
            this.searchFemale = searchFemale;
        }

        public void setSf(String searchFemale) {
            this.searchFemale = searchFemale;
        }

        public String getSearchMale() {
            return searchMale;
        }

        public void setSearchMale(String searchMale) {
            this.searchMale = searchMale;
        }

        public void setSm(String searchMale) {
            this.searchMale = searchMale;
        }

        public List<Long> getExcludeOpponents() {
            return excludeOpponents;
        }

        public void setExcludeOpponents(List<Long> excludeOpponents) {
            this.excludeOpponents = excludeOpponents;
        }

        public void setExo(List<Long> excludeOpponents) {
            this.excludeOpponents = excludeOpponents;
        }

        public Set<Gender> getGenders() {
            Set<Gender> genders = new HashSet<>();
            if ("Y".equals(searchFemale)) {
                genders.add(Gender.FEMALE);
            }
            if ("Y".equals(searchMale)) {
                genders.add(Gender.MALE);
            }
            return genders;
        }
    }
}
