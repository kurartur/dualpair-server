package lt.dualpair.server.domain.model.user;

import lt.dualpair.server.domain.model.geo.Location;

import javax.persistence.*;

@Entity
@Table(name = "user_locations")
public class UserLocation {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private Location location;

    private UserLocation() {}

    public UserLocation(User user, Double latitude, Double longitude, String countryCode, String city) {
        this.user = user;
        this.location = new Location(latitude, longitude, countryCode, city);
    }
}
