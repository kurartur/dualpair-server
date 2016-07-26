package com.artur.dualpair.server.domain.model.user;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_devices")
public class Device implements Serializable {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Device() {}

    public Device(String id, User user) {
        this.id = id;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
}
