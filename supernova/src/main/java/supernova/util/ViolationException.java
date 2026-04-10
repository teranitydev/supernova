package supernova.util;

public class ViolationException extends RuntimeException {

    public ViolationException(String message) {
        super("Result operation violated: " + message);
    }
}
