package com.artur.dualpair.server.infrastructure.notification;

public class Notification {

    private Long userId;
    private String message;

    public Notification(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "message='" + message + '\'' +
                ", userId=" + userId +
                '}';
    }
}
