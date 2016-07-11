package com.artur.dualpair.server.domain.model;

import com.artur.dualpair.server.domain.model.user.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "matches")
public class Match implements Serializable {

    public enum Response {UNDEFINED, NO, YES}

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "opponent_id")
    private User opponent;

    private Integer distance;

    @Enumerated
    private Response response = Response.UNDEFINED;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getOpponent() {
        return opponent;
    }

    public void setOpponent(User opponent) {
        this.opponent = opponent;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
