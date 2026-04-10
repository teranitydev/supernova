package supernova.util;

import java.util.List;

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
    private static ViolationHandler HANDLER;

    public static ViolationHandler getHandler() {
        if (HANDLER == null) {
            HANDLER = new ExceptionViolationHandler();
        }

        return HANDLER;
    }

    public static void setHandler(ViolationHandler HANDLER) {
        ViolationHandler.HANDLER = HANDLER;
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
