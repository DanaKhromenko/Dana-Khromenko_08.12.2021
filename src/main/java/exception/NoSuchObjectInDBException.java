package exception;

public class NoSuchObjectInDBException extends RuntimeException {
    public NoSuchObjectInDBException(String message) {
        super(message);
    }
}
