package lt.dualpair.server.interfaces.web.controller.rest.match;

import lt.dualpair.server.domain.model.match.Match;
import lt.dualpair.server.domain.model.match.MatchRequestBuilder;
import lt.dualpair.server.domain.model.match.MatchRequestException;
import lt.dualpair.server.domain.model.match.UserAwareMatch;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import lt.dualpair.server.interfaces.resource.match.MatchResourceAssembler;
import lt.dualpair.server.service.match.MatchService;
import lt.dualpair.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(method = RequestMethod.GET, value = "/match/next")
    public ResponseEntity next(@Valid SearchQuery searchQuery, @ActiveUser User principal) throws MatchRequestException {

        User freshUser = userService.loadUserById(principal.getId());

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
        return ResponseEntity.ok().body(matchResourceAssembler.toResource(new UserAwareMatch(principal, match)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/match/{matchId:[0-9]+}")
    public ResponseEntity match(@PathVariable Long matchId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Autowired
    public void setMatchService(MatchService matchService) {
        this.matchService = matchService;
    }

    @Autowired
    public void setMatchResourceAssembler(MatchResourceAssembler matchResourceAssembler) {
        this.matchResourceAssembler = matchResourceAssembler;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
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

        public Set<User.Gender> getGenders() {
            Set<User.Gender> genders = new HashSet<>();
            if ("Y".equals(searchFemale)) {
                genders.add(User.Gender.FEMALE);
            }
            if ("Y".equals(searchMale)) {
                genders.add(User.Gender.MALE);
            }
            return genders;
        }
    }
}
