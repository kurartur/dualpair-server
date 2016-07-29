package lt.dualpair.server.service.socionics.test;

public class SocionicsTestException extends Exception {

    public static final String sociotypeNotFound = "Sociotype not found";
    public static final String codeNotFoundInResponse = "Code not found in response";
    public static final String unableToEvaluateRemotely = "Unable to evaluate remotely";
    public static final String evaluateRequestError = "Evaluate request error";

    public SocionicsTestException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocionicsTestException(String message) {
        super(message);
    }

}
