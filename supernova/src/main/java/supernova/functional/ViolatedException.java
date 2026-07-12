package supernova.functional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Thrown when operation of {@link Result} is violated.
 */
public class ViolatedException extends RuntimeException {

    private final Collection<Violation> violations;

    /**
     * The default constructor.
     *
     * @param violations Collection of violations.
     */
    public ViolatedException(Collection<Violation> violations) {
        super("Result operation is violated: " + formatViolations(violations));
        this.violations = violations;
    }

    public Collection<Violation> getViolations() {
        return violations;
    }

    private static String formatViolations(Collection<Violation> violations) {
        if (violations == null || violations.isEmpty()) return "None";
        return violations.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
