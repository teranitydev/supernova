package supernova.util;

/**
 * {@link ViolationHandler} that handle {@link Exception}
 */
public class ExceptionViolationHandler extends ViolationHandler<Exception>{

    @Override
    public void handle(Violation<Exception> violation) {
        Exception e = violation.value();

        if (e instanceof RuntimeException runtime) {
            throw runtime;
        }

        throw new RuntimeException("Violation triggered an exception: " + e.getMessage(), e);
    }
}
