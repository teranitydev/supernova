package supernova.util;

import java.util.List;
import java.util.Objects;

/**
 * An abstract handler for processing violation.
 *
 * <p>Users can extend this class to define custom behaviour for handling violations,
 * such as logging, throwing exceptions, or any other strategy.
 *
 * @author Izhar
 * @since 0.0.1
 */
public abstract class ViolationHandler {

    /**
     * The violation handler that will be used for all {@link Result}
     * operations.
     *
     * <p>Developers can change the handler anything they want.
     */
    private static volatile ViolationHandler HANDLER;

    public static void ignore() {
        setHandler(new IgnoreViolationHandler());
    }

    public static ViolationHandler getHandler() {
        if (HANDLER == null) {
            synchronized (ViolationHandler.class) {
                if (HANDLER == null) {
                    HANDLER = new ExceptionViolationHandler();
                }
            }
        }
        return HANDLER;
    }


    public static synchronized void setHandler(ViolationHandler handler) {
        Objects.requireNonNull(handler, "handler cannot be null");
        HANDLER = handler;
    }
    /**
     * Handle a single violation.
     *
     * @param violation the violation instance to process
     */
    public abstract void handle(Violation violation);

    /**
     * Handle multiple violations
     *
     * @param violations list of violations
     */
    public abstract void handle(List<Violation> violations);
}
