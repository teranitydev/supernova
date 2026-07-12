package supernova.functional;

import java.util.*;
import java.util.function.Consumer;

/**
 * An immutable container that may or may not contain a {@code null}able value and zero or more violation.
 * A result that have no violations is stated as successful; otherwise it is stated as violated.
 *
 * <p>A violated result is also can be interpreted as failed operation. It has the same meaning as error.
 * but violation will always contain error field and error message.</p>
 *
 * <p>A successful result may still contain a {@code null} value.</p
 *
 * <p>Violation represent as error and can contain any type of object such as string, integer, custom
 * object, etc.</p>
 *
 * @param <T> The type of the contained value.
 * @author Izhar Atharzi
 * @since 1.0.0
 */
public class Result<T> {

    /**
     * Nullable value which does not trigger anything in result.
     */
    private final T value;

    /**
     * Collection of violations; if empty, state as successful.
     */
    private final Collection<Violation> violations;

    /**
     * Constructs an instance with value and violations.
     *
     * @param value The value of the result
     * @param violations Collection of violations; if null, it is treated as an empty collection.
     */
    private Result(T value, Collection<Violation> violations) {
        this.value = value;
        this.violations = List.copyOf(violations);
    }

    /**
     * Returns a successful {@link Result} with a value.
     *
     * @param value The value
     * @return a successful {@link Result} with the value
     * @param <T> The type of value
     */
    public static <T> Result<T> successful(T value) {
        return new Result<>(value, Collections.emptyList());
    }

    /**
     * Returns a successful {@link Result} for void type.
     *
     * @return a successful {@link Result} for void type
     */
    public static Result<Void> successful() {
        return new Result<>(null, Collections.emptyList());
    }

    /**
     * Returns violated {@link Result} with collection of violations.
     *
     * @param violations List of violations
     * @return Violated {@link Result} with collection of violations
     * @param <T> The type of value
     */
    public static <T> Result<T> violated(List<Violation> violations) {
        return new Result<>(null, Collections.unmodifiableCollection(violations));
    }

    /**
     * Returns violated {@link Result} with collection of violations.
     *
     * @param violations List of violations
     * @return Violated {@link Result} with collection of violations.
     * @param <T> The type of value
     */
    public static <T> Result<T> violated(Violation... violations) {
        return violated(List.of(violations));
    }

    /**
     * Returns violated {@link Result} with single violation.
     *
     * @param violation A violation
     * @return Violated {@link Result} with single violation.
     * @param <T> The type of value
     */
    public static <T> Result<T> violated(Violation violation) {
        return violated(List.of(violation));
    }

    public static <T> Result<T> of(T value, List<Violation> violations) {
        return new Result<>(value, violations);
    }

    /**
     * Checks if the result is successful.
     *
     * @return {@code true} if the result contains no violations.
     */
    public boolean isSuccessful() {
        return violations.isEmpty();
    }

    /**
     * Checks if the result is failed or violated.
     *
     * @return {@code true} if the result contains violations.
     */
    public boolean isViolated() {
        return !violations.isEmpty();
    }

    /**
     * If result is successful then performs the given action with the reference, otherwise
     * perform nothing.
     */
    public Result<T> whenSuccessful(Consumer<? super T> action) {
        if (isSuccessful()) {
            action.accept(value);
        }

        return this;
    }

    /**
     * If result is violated then performs the given action with the violation, otherwise
     * perform nothing.
     */
    public Result<T> whenViolated(Consumer<Collection<Violation>> action) {
        if (isViolated()) {
            action.accept(violations);
        }

        return this;
    }

    /**
     * If the result is successful returns the value.
     *
     * @throws ViolatedException if the result is violated
     * @return The instance of the value
     */
    public T get() {
        if (isViolated()) {
            throw new ViolatedException(violations);
        }
        return value;
    }

    /**
     * If the result is successful returns the value, otherwise returns the default value.
     *
     * @param defaultValue The instance of the default value
     * @return The value if the result is successful or default value if violated
     */
    public T getOrElse(T defaultValue) {
        if (isViolated()) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets a collection of violations.
     *
     * @return Unmodifiable collection of violations
     */
    public Collection<Violation> violations() {
        return violations;
    }
}