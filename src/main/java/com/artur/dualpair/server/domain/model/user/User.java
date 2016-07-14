package com.artur.dualpair.server.domain.model.user;

import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUserDetails;
import org.thymeleaf.util.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements SocialUserDetails, Serializable {

    public enum Status { INITIAL, ACTIVE, BLOCKED }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String username;

    private String email;

    @Enumerated
    private Status status = Status.INITIAL;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updated;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<UserAccount> userAccounts;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "users_sociotypes", joinColumns = {
            @JoinColumn(name = "user_id", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "sociotype_id",
                    nullable = false, updatable = false) })
    private Set<Sociotype> sociotypes = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SearchParameters searchParameters;

    private String name;

    @Embedded
    private AgeInfo ageInfo = new AgeInfo(null);

    private Gender gender;

    private String description;

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
    public String getUserId() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void activate() {

    }

    public Status getStatus() {
        return status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
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
            throw new IllegalArgumentException("User must have 1 or 2 sociotypes");
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
        this.name = name;
    }

    public Integer getAge() {
        return ageInfo == null ? null : ageInfo.getAge(); // TODO why ageInfo is null?
    }

    public Date getDateOfBirth() {
        return ageInfo == null ? null : ageInfo.getDateOfBirth(); // TODO why ageInfo is null?
    }

    public void setDateOfBirth(Date dateOfBirth) {
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
        this.description = description;
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
        return getUserId();
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
