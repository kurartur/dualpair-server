package lt.dualpair.server.domain.model.match;

import java.util.HashMap;
import java.util.Map;

public enum Response {

    UNDEFINED("U"),
    NO("N"),
    YES("Y");

    private String code;
    private static Map<String, Response> responsesByCode = new HashMap<>();

    static {
        for (Response response : Response.values()) {
            responsesByCode.put(response.code, response);
        }
    }

    Response(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Response fromCode(String code) {
        return responsesByCode.get(code);
    }

}