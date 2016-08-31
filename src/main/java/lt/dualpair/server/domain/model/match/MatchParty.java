package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.user.User;

import javax.persistence.*;

@Entity
@Table(name = "match_parties")
public class MatchParty {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Response response = Response.UNDEFINED;

    public MatchParty() {}

    public MatchParty(Match match, User user) {
        this.match = match;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
