package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.geo.Location;
import lt.dualpair.server.domain.model.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "search_parameters")
public class SearchParameters implements Serializable {

    @Id @GeneratedValue(generator = "customForeignGenerator")
    @org.hibernate.annotations.GenericGenerator(
            name = "customForeignGenerator",
            strategy = "foreign",
            parameters = @org.hibernate.annotations.Parameter(name = "property", value = "user")
    )
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private User user;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "search_male", length = 1)
    @Type(type = "yes_no")
    private boolean searchMale;

    @Column(name = "search_female", length = 1)
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

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
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

    public void setFrom(SearchParameters s) {
        setSearchMale(s.getSearchMale());
        setSearchFemale(s.getSearchFemale());
        setMinAge(s.getMinAge());
        setMaxAge(s.getMaxAge());
        setLocation(s.getLocation());
    }

}
