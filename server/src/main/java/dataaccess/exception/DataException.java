package dataaccess.exception;

public class DataException extends Exception {
    public DataException(String message) {
        super(message);
    }
    public DataException(String message, Throwable ex) {
        super(message, ex);
    }
}
