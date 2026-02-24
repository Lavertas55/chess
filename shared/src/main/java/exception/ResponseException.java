package exception;

import com.google.gson.Gson;

import java.util.HashMap;import java.util.Map;

public class ResponseException extends Exception {

    private final Code code;

    public enum Code {
        FORBIDDEN,
        UNAUTHORIZED,
        NOT_FOUND,
        BAD_REQUEST,
        SERVER_ERROR
    }

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        var serializer = new Gson();
        return serializer.toJson(Map.of("code", code, "message", getMessage()));
    }

    public static ResponseException fromJson(String json) {
        var serializer = new Gson();
        var map = serializer.fromJson(json, HashMap.class);
        Code code = Code.valueOf(map.get("code").toString());
        String message = map.get("message").toString();
        return new ResponseException(code, message);
    }

    public int toHttpStatusCode() {
        return switch (code) {
            case FORBIDDEN -> 403;
            case UNAUTHORIZED -> 401;
            case NOT_FOUND -> 404;
            case BAD_REQUEST -> 400;
            case SERVER_ERROR -> 500;
        };
    }

    public Code getCode() {
        return code;
    }
}
