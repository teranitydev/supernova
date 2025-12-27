package supernova.util;

/**
 * This exception is used when {@link Result} is handling {@code handleViolation} and found
 * no violation handler for specified class type.
 */
public class ViolationHandlerNotFoundException extends RuntimeException {

    public ViolationHandlerNotFoundException(String message) {
        super(message);
    }
}
