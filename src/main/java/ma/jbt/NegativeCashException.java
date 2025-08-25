package ma.jbt;

public class NegativeCashException extends RuntimeException {
    public NegativeCashException(String message) {
        super(message);
    }
}
