package lt.dualpair.server.domain.model.user;

import java.util.HashMap;
import java.util.Map;

public enum RelationshipStatus {

    NONE(null),
    SINGLE("SI"),
    IN_RELATIONSHIP("IR");

    private String code;
    private static Map<String, RelationshipStatus> statusesByCode = new HashMap<>();

    static {
        for (RelationshipStatus status : RelationshipStatus.values()) {
            statusesByCode.put(status.code, status);
        }
    }

    RelationshipStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RelationshipStatus fromCode(String code) {
        return statusesByCode.get(code);
    }

}
