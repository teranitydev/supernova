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
    public ResultBuilder<T> value(T value) {
        this.value = value;

        return this;
    }

    /**
     * Set new value if the condition is true.
     */
    public ResultBuilder<T> valueIf(boolean condition, T value) {
        Objects.requireNonNull(value);

        if (condition) {
            return value(value);
        }

        return this;
    }

    /**
     * Set new value if {@link Supplier} conditional check is true.
     *
     * @return builder
     */
    public ResultBuilder<T> valueIf(Supplier<Boolean> condition, T value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(condition);

        return valueIf(condition.get(), value);
    }

    /**
     * Violate the {@link Result} with {@code E} violation, which means the {@link Result} of the operation
     * is failed.
     *
     * @param violation the type of violation value
     * @return builder
     */
    public <E extends Violation> ResultBuilder<T> violate(E violation) {
        Objects.requireNonNull(violation);

        if (violations.contains(violation)) return this;

        violations.add(violation);

        return this;
    }

    /**
     * Violate the {@link Result} with conditional check and {@code E} violation.
     *
     * @param condition the condition of the violation,
     *                  true - violation
     *                  false - not violation
     * @param violation the type of violation value
     * @return builder
     */
    public <E extends Violation> ResultBuilder<T> violateIf(boolean condition, E violation) {
        Objects.requireNonNull(violation);

        if (condition) {
            return violate(violation);
        }

        return this;
    }

    /**
     * Violate the {@link Result} with conditional check of {@link Supplier} and {@code E} violation.
     *
     * @param condition the condition of the violation,
     *                  true - violation
     *                  false - not violation
     * @param violationValue the type of violation value
     * @return builder
     */
    public <E extends Violation> ResultBuilder<T> violateIf(Supplier<Boolean> condition, E violationValue) {
        Objects.requireNonNull(violationValue);
        Objects.requireNonNull(condition);

        return violateIf(condition.get(), violationValue);
    }

    /**
     * Build the {@link Result} and returns the Result
     */
    public Result<T> build() {
        if (violations.isEmpty() && value == null) {
            throw new IllegalStateException("Cannot build Result because both violations and value are empty");
        }

        if (!violations.isEmpty() && value != null) {
            throw new IllegalStateException("Cannot build Result because both violations and value are exists, Result is meant to be one-state container");
        }

        return Result.of(value, violations);
    }
}
