package com.artur.dualpair.server.domain.model.match;

import com.artur.dualpair.server.domain.model.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "search_parameters")
public class SearchParameters {

    @Id @GeneratedValue(generator = "customForeignGenerator")
    @org.hibernate.annotations.GenericGenerator(
            name = "customForeignGenerator",
            strategy = "foreign",
            parameters = @org.hibernate.annotations.Parameter(name = "property", value = "user")
    )
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User user;

    @Column(name = "min_age")
    private int minAge;

    @Column(name = "max_age")
    private int maxAge;

    @Column(name = "search_male")
    @Type(type = "yes_no")
    private boolean searchMale;

    @Column(name = "search_female")
    @Type(type = "yes_no")
    private boolean searchFemale;

    @Embedded
    private Location location;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public boolean getSearchFemale() {
        return searchFemale;
    }

    public void setSearchFemale(boolean searchFemale) {
        this.searchFemale = searchFemale;
    }

    public boolean getSearchMale() {
        return searchMale;
    }

    public void setSearchMale(boolean searchMale) {
        this.searchMale = searchMale;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }



}
