package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRequestBuilder;
import lt.dualpair.server.interfaces.resource.user.UserResourceAssembler;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.service.user.UserSearchService;
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
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserSearchController {

    private UserSearchService userSearchService;
    private UserResourceAssembler userResourceAssembler;
    private UserService userService;

    @Inject
    public UserSearchController(UserSearchService userSearchService, UserResourceAssembler userResourceAssembler, UserService userService) {
        this.userSearchService = userSearchService;
        this.userResourceAssembler = userResourceAssembler;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public ResponseEntity find(@Valid SearchQuery searchQuery, @ActiveUser UserDetails principal) {

        User freshPrincipal = userService.loadUserById(new Long(principal.getUsername()));

        UserRequestBuilder builder = UserRequestBuilder.findFor(freshPrincipal)
                .ageRange(searchQuery.getMinAge(), searchQuery.getMaxAge())
                .genders(searchQuery.getGenders());

        if (searchQuery.getExcludeOpponents() != null && !searchQuery.getExcludeOpponents().isEmpty()) {
            builder.excludeOpponents(new HashSet<>(searchQuery.getExcludeOpponents()));
        }

        Optional<User> result = userSearchService.findOne(builder.build());
        if (!result.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(result.get(), false, false)));

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
