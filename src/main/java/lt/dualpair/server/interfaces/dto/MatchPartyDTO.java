package lt.dualpair.server.interfaces.dto;

public class MatchPartyDTO {

    private UserDTO user;
    private String response;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
