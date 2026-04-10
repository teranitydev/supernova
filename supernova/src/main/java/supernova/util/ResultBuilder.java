package supernova.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The builder of {@link Result}
 */
public class ResultBuilder<T> {

    private final List<Violation> violations;

    private T value;

    ResultBuilder(List<Violation> violations, T value) {
        this.violations = violations;
        this.value = value;
    }

    /**
     * Setter for {@code T} value.
     *
     * @param value the type of value
     */
    public void value(T value) {
        this.value = value;
    }

    /**
     * Set new value if the condition is true.
     */
    public void valueIf(boolean condition, T value) {
        Objects.requireNonNull(value);

        if (condition) {
            value(value);
        }
    }

    /**
     * Set new value if {@link Supplier} conditional check is true.
     */
    public void valueIf(Supplier<Boolean> condition, T value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(condition);

        valueIf(condition.get(), value);
    }

    /**
     * Violate the {@link Result} with {@code E} violation, which means the {@link Result} of the operation
     * is failed.
     *
     * @param violation the type of violation value
     */
    public <E extends Violation> void violate(E violation) {
        Objects.requireNonNull(violation);

        if (violations.contains(violation)) return;

        if (value != null) {
            throw new IllegalStateException("Cannot violate Result because Result already stated as successful");
        }

        violations.add(violation);
    }

    /**
     * Violate the {@link Result} with conditional check and {@code E} violation.
     *
     * @param condition the condition of the violation,
     *                  true - violation
     *                  false - not violation
     * @param violation the type of violation value
     */
    public <E extends Violation> void violateIf(boolean condition, E violation) {
        Objects.requireNonNull(violation);

        if (condition) {
            violate(violation);
        }
    }

    /**
     * Violate the {@link Result} with conditional check of {@link Supplier} and {@code E} violation.
     *
     * @param condition the condition of the violation,
     *                  true - violation
     *                  false - not violation
     * @param violationValue the type of violation value
     */
    public <E extends Violation> void violateIf(Supplier<Boolean> condition, E violationValue) {
        Objects.requireNonNull(violationValue);
        Objects.requireNonNull(condition);

        violateIf(condition.get(), violationValue);
    }

    /**
     * Build the {@link Result} and returns the Result
     */
    public Result<T> build() {
        return Result.of(value, violations);
    }
}
