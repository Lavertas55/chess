package dataaccess.exception;

public class BadDataException extends DataException {
    public BadDataException(String message) {
        super(message);
    }
    public BadDataException(String message, Throwable ex) {
        super(message, ex);
    }
}
