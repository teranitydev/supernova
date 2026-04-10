package supernova.util;

/**
 * An interface representing a violation or error in an operation.
 *
 * <p>The {@code Violation} can hold any type of value, such as a message, or custom object,
 * depending on the context of the operation.</p>
 *
 * @author Izhar Atharzi
 * @since 0.0.1
 */
public interface Violation {

    String getMessage();
}