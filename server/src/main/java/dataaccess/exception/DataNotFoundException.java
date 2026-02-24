package dataaccess.exception;

public class DataNotFoundException extends DataException {
    public DataNotFoundException(String message) {
        super(message);
    }
    public DataNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
