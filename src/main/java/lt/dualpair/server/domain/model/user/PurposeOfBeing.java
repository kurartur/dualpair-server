package lt.dualpair.server.domain.model.user;

import java.util.HashMap;
import java.util.Map;

public enum PurposeOfBeing {

    FIND_FRIEND("FIFR"),
    FIND_LOVE("FILO");

    private String code;
    private static Map<String, PurposeOfBeing> purposesByCode = new HashMap<>();

    static {
        for (PurposeOfBeing purpose : PurposeOfBeing.values()) {
            purposesByCode.put(purpose.code, purpose);
        }
    }

    PurposeOfBeing(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PurposeOfBeing fromCode(String code) {
        return purposesByCode.get(code);
    }

}
