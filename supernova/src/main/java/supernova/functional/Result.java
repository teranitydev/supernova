package supernova.functional;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * An immutable container that may or may not contain a {@code null}able value and zero or more violation.
 * A result that have no violations is stated as successful; otherwise it is stated as violated.
 *
 * <p>A result also can have warnings which don't cause any fatal errors for the operation but is important
 * enough to worth knowing. Warnings can only be added in {@link ResultBuilder}.</p>
 *
 * <p>A violated result is also can be interpreted as failed operation. It has the same meaning as error.
 * but violation will always contain error field and error message.</p>
 *
 * <p>A successful result may still contain a {@code null} value.</p
 *
 * <p>Violation represent as error and can contain any type of object such as string, integer, custom
 * object, etc.</p>
 *
 * @param <T> the type of the contained value.
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
     * Collections of warnings
     */
    private final Collection<Warning> warnings;

    /**
     * Constructs an instance with value and violations.
     *
     * @param value the value of the result
     * @param violations collection of violations; if null, it is treated as an empty collection.
     */
    private Result(T value, Collection<Violation> violations, Collection<Warning> warnings) {
        this.value = value;
        this.violations = List.copyOf(violations);
        this.warnings = List.copyOf(warnings);
    }

    /**
     * Creates a {@link Result} builder.
     *
     * @return a Result builder
     * @param <T> the type of value
     */
    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>();
    }

    /**
     * Returns a successful {@link Result} with a value.
     *
     * @param value The value
     * @return a successful {@link Result} with the value
     * @param <T> the type of value
     */
    public static <T> Result<T> successful(T value) {
        return new Result<>(value, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Returns a successful {@link Result} with a value and a warning.
     *
     * @param value the object of the value
     * @param warning the instance of the warning
     * @return a successful {@link Result} with a value and a warning
     * @param <T> the type of value
     */
    public static <T> Result<T> successful(T value, Warning warning) {
        return new Result<>(value, Collections.emptyList(), List.of(warning));
    }

    /**
     * Returns a successful {@link Result} with a value and warnings.
     *
     * @param value the object of the value
     * @param warnings list of warnings
     * @return a successful {@link Result} with a value and a warning
     * @param <T> the type of value
     */
    public static <T> Result<T> successful(T value, Warning... warnings) {
        return new Result<>(value, Collections.emptyList(), List.of(warnings));
    }

    /**
     * Returns a successful {@link Result} with a value and list of warnings.
     *
     * @param value the object of the value
     * @param warnings list of warnings
     * @return a successful {@link Result} with a value and a warning
     * @param <T> the type of value
     */
    public static <T> Result<T> successful(T value, List<Warning> warnings) {
        return new Result<>(value, Collections.emptyList(), warnings);
    }

    /**
     * Returns a successful {@link Result} for void type.
     *
     * @return a successful {@link Result} for void type
     */
    public static Result<Void> successful() {
        return new Result<>(null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Returns a successful {@link Result} for void type with a warning.
     *
     * @param warning the instance of the warning
     * @return a successful {@link Result}
     * @param <T> the type of the value
     */
    public static <T> Result<T> successful(Warning warning) {
        return new Result<>(null, Collections.emptyList(), List.of(warning));
    }

    /**
     * Returns a successful {@link Result} for void type with warnings.
     *
     * @param warnings list of warnings
     * @return a successful {@link Result}
     * @param <T> the type of the value
     */
    public static <T> Result<T> successful(Warning... warnings) {
        return new Result<>(null, Collections.emptyList(), List.of(warnings));
    }

    /**
     * Returns a successful {@link Result} for void type with list of warnings.
     *
     * @param warnings list of warnings
     * @return a successful {@link Result}
     * @param <T> the type of the value
     */
    public static <T> Result<T> successful(List<Warning> warnings) {
        return new Result<>(null, Collections.emptyList(), warnings);
    }

    /**
     * Returns violated {@link Result} with collection of violations.
     *
     * @param violations list of violations
     * @return violated {@link Result} with collection of violations
     * @param <T> the type of value
     */
    public static <T> Result<T> violated(List<Violation> violations) {
        return new Result<>(
                null,
                violations,
                Collections.emptyList()
        );
    }

    /**
     * Returns violated {@link Result} with collection of violations.
     *
     * @param violations list of violations
     * @return violated {@link Result} with collection of violations.
     * @param <T> the type of value
     */
    public static <T> Result<T> violated(Violation... violations) {
        return violated(List.of(violations));
    }

    /**
     * Returns violated {@link Result} with single violation.
     *
     * @param violation a violation
     * @return violated {@link Result} with single violation.
     * @param <T> the type of value
     */
    public static <T> Result<T> violated(Violation violation) {
        return violated(List.of(violation));
    }

    /**
     * Creates a result with all the params.
     */
    public static <T> Result<T> of(T value, List<Violation> violations, List<Warning> warnings) {
        return new Result<>(value, violations, warnings);
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
     * Checks if the result have warnings.
     *
     * @return {@code true} if the result contains warnings.
     */
    public boolean hasWarning() {
        return !warnings.isEmpty();
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
     * If result have warning then performs the given action with the warnings, otherwise
     * perform nothing.
     */
    public Result<T> whenWarning(Consumer<Collection<Warning>> action) {
        if (hasWarning()) {
            action.accept(warnings);
        }

        return this;
    }

    /**
     * If the result is successful returns the value.
     *
     * @throws ViolatedException if the result is violated
     * @return the instance of the value
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
     * @param defaultValue the instance of the default value
     * @return the value if the result is successful or default value if violated
     */
    public T getOrElse(T defaultValue) {
        if (isViolated()) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Returns a sequential {@link Stream} containing the value if this result is
     * successful. Otherwise, returns an empty stream.
     *
     * <p>If this result is successful and its value is {@code null}, an empty
     * stream is returned.
     *
     * @return a stream containing the value if this result is successful and
     *         non-{@code null}; otherwise an empty stream
     */
    public Stream<T> stream() {
        return isSuccessful()
                ? Stream.ofNullable(value)
                : Stream.empty();
    }

    /**
     * Gets a collection of violations.
     *
     * @return unmodifiable collection of violations
     */
    public Collection<Violation> violations() {
        return violations;
    }

    /**
     * Gets a collection of warnings.
     *
     * @return unmodifiable collection of warnings
     */
    public Collection<Warning> warnings() {
        return warnings;
    }
}