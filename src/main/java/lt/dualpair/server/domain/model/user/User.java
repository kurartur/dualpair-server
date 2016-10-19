package lt.dualpair.server.domain.model.user;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.util.Assert;
import org.thymeleaf.util.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements SocialUserDetails, Serializable {

    public enum Status { INITIAL, ACTIVE, BLOCKED }

    private static final int NAME_LENGTH = 100;
    private static final int DESCRIPTION_LENGTH = 255;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String username;

    private String email;

    @Enumerated
    private Status status = Status.INITIAL;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date dateUpdated;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<UserAccount> userAccounts;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "users_sociotypes", joinColumns = {
            @JoinColumn(name = "user_id", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "sociotype_id",
                    nullable = false, updatable = false) })
    private Set<Sociotype> sociotypes = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SearchParameters searchParameters = new SearchParameters();

    private String name;

    @Embedded
    private AgeInfo ageInfo = new AgeInfo(null);

    private Gender gender;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserLocation> locations = new ArrayList<>();

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    @Deprecated
    public String getUserId() {
        return getUsername();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Status getStatus() {
        return status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date date_updated) {
        this.dateUpdated = date_updated;
    }

    public Set<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(Set<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }

    public Set<Sociotype> getSociotypes() {
        return sociotypes;
    }

    public Sociotype getRandomSociotype() {
        if (sociotypes.size() == 1) {
            return sociotypes.iterator().next();
        } else {
            return new ArrayList<>(sociotypes).get((Math.random() <= 0.5) ? 1 : 2);
        }
    }

    public void setSociotypes(Set<Sociotype> sociotypes) {
        Validate.notNull(sociotypes, "Sociotypes are mandatory");
        if (sociotypes.size() > 2 || sociotypes.size() == 0) {
            throw new IllegalArgumentException("Invalid sociotype code count. Must be 1 or 2");
        }
        this.sociotypes = sociotypes;
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(SearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Assert.hasText(name);
        if (name.length() > NAME_LENGTH) {
            throw new IllegalArgumentException("Name length can't be greater than " + NAME_LENGTH);
        }
        this.name = name;
    }

    public Integer getAge() {
        return ageInfo == null ? null : ageInfo.getAge();
    }

    public Date getDateOfBirth() {
        return ageInfo == null ? null : ageInfo.getDateOfBirth();
    }

    public void setDateOfBirth(Date dateOfBirth) {
        Assert.notNull(dateOfBirth);
        this.ageInfo = new AgeInfo(dateOfBirth);
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description.length() > DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description length can't be greater than " + DESCRIPTION_LENGTH);
        }
        this.description = description;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<UserLocation> getLocations() {
        return locations;
    }

    public UserLocation getRecentLocation() {
        return locations.isEmpty() ? null : locations.get(0);
    }

    public void addLocation(UserLocation location) {
        this.locations.clear();
        this.locations.add(location);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == Status.ACTIVE;
    }

    public enum Gender {
        MALE("M"), FEMALE("F");
        private String code;
        private static Map<String, Gender> gendersByCode = new HashMap<>();
        static {
            for (Gender gender: Gender.values()) {
                gendersByCode.put(gender.code, gender);
            }
        }
        Gender(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
        public static Gender fromCode(String code) {
            return gendersByCode.get(code);
        }
    }

}
