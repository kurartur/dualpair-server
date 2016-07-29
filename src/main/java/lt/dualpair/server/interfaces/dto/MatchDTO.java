package lt.dualpair.server.interfaces.dto;

public class MatchDTO {

    private UserDTO user;
    private UserDTO opponent;
    private String response;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserDTO getOpponent() {
        return opponent;
    }

    public void setOpponent(UserDTO opponent) {
        this.opponent = opponent;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
