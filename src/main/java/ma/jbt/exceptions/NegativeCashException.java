package ma.jbt.exceptions;

public class NegativeCashException extends RuntimeException {
    public NegativeCashException(String message) {
        super(message);
    }
}
