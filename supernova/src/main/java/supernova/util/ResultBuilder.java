package supernova.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The builder of {@link Result}
 */
public class ResultBuilder<T> {

    private final List<Violation<?>> violations;

    private T value;

    ResultBuilder(List<Violation<?>> violations, T value) {
        this.violations = violations;
        this.value = value;
    }

    /**
     * Setter for {@code T} value.
     *
     * @param value the type of value
     */
    public void value(T value) {
        Objects.requireNonNull(value);

        if (this.value != null) throw new IllegalStateException("Cannot set new value for Result because Result already stated as successful");

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
     * @param violationValue the type of violation value
     */
    public <E> void violate(E violationValue) {
        Objects.requireNonNull(violationValue);

        for (Violation<?> violation : violations) {
            if (violation.value().equals(violationValue)) {
                return;
            }
        }

        if (value != null) {
            throw new IllegalStateException("Cannot violate Result because Result already stated as successful");
        }

        final Violation<E> violation = new Violation<>(violationValue);
        violations.add(violation);
    }

    /**
     * Violate the {@link Result} with conditional check and {@code E} violation.
     *
     * @param condition the condition of the violation,
     *                  true - violation
     *                  false - not violation
     * @param violationValue the type of violation value
     */
    public <E> void violateIf(boolean condition, E violationValue) {
        Objects.requireNonNull(violationValue);

        if (condition) {
            violate(violationValue);
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
    public <E> void violateIf(Supplier<Boolean> condition, E violationValue) {
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
