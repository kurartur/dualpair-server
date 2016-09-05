package lt.dualpair.server.infrastructure.notification;

public class Notification<T> {

    private Long userId;
    private NotificationType notificationType;
    private T payload;

    public Notification(Long userId, NotificationType notificationType, T payload) {
        this.userId = userId;
        this.notificationType = notificationType;
        this.payload = payload;
    }

    public Long getUserId() {
        return userId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public T getPayload() {
        return payload;
    }
}
