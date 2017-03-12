package lt.dualpair.server.domain.model.user;

import java.util.HashMap;
import java.util.Map;

public enum Gender {
    MALE("M"), FEMALE("F");
    private String code;
    private static Map<String, Gender> gendersByCode = new HashMap<>();

    static {
        for (Gender gender : Gender.values()) {
            gendersByCode.put(gender.code, gender);
        }
    }

    Gender(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Gender fromCode(String code) {
        return gendersByCode.get(code);
    }
}
