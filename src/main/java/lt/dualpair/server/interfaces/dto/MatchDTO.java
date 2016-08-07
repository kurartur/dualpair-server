package lt.dualpair.server.interfaces.dto;

public class MatchDTO {

    private Long id;
    private MatchPartyDTO user;
    private MatchPartyDTO opponent;
    private Integer distance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatchPartyDTO getUser() {
        return user;
    }

    public void setUser(MatchPartyDTO user) {
        this.user = user;
    }

    public MatchPartyDTO getOpponent() {
        return opponent;
    }

    public void setOpponent(MatchPartyDTO opponent) {
        this.opponent = opponent;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}
