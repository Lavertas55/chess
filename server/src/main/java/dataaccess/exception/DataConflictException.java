package dataaccess.exception;

public class DataConflictException extends DataException {
    public DataConflictException(String message) {
        super(message);
    }
    public DataConflictException(String message, Throwable ex) {
        super(message, ex);
    }
}
