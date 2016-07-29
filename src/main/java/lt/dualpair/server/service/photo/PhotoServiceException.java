package lt.dualpair.server.service.photo;

public class PhotoServiceException extends Exception {

    public PhotoServiceException(String message) {
        super(message);
    }

    public PhotoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
