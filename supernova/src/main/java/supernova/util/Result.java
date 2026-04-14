package supernova.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A container which may or may not contain a value, with also having violation(s).
 * When developers use either {@code success()} or {@code violations{}},
 * it will create {@link Result} that stated either success or violated that you cannot set new
 * value or violate.
 *
 * <p>Developers also can create builder of {@link Result} by using {@code Result.builder()}
 * and returns {@link ResultBuilder}
 *
 * <p>If {@link Result} does not have violation(s) it means operation is succeeded,
 * otherwise, if {@link Result} have violation(s) it means operation is failed.
 *
 * <p>Success {@link Result} would require NO violations at all, and can have nullable value.
 *
 * <p>Violation(s) or also called error(s) can be {@link String} of error message, custom object, etc.
 *
 * @param <T> the type of the value
 * @author Izhar Atharzi
 * @since 0.0.1
 */
public class Result<T> {

    /**
     * The instance container that containing {@link Class} type of violation and
     * the violation value itself.
     */
    private final List<Violation> violations;

    /**
     * The instance of the value reference.
     */
    private final T value;

    /**
     * The default constructor of {@link Result}.
     *
     * @param value the type of the value reference
     */
    private Result(T value, List<Violation> violations) {
        this.value = value;
        this.violations = violations;
    }

    /**
     * Returns {@link Result} for {@code void}-type.
     *
     * @return An empty {@link Result}
     */
    public static Result<Void> success() {
        return new Result<>(null, Collections.emptyList());
    }

    /**
     * Returns {@link Result} containing the value.
     *
     * @param value the type of the value reference
     * @return An {@link Result} with {@code T} value
     * @param <T> the type of the value
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, Collections.emptyList());
    }

    /**
     * Returns {@link Result} containing a violation value.
     *
     * @param violation the type of violation value
     * @return An {@link Result} with violation
     * @param <T> the type of the value
     */
    public static <T> Result<T> violation(Violation violation) {
        return violations(List.of(violation));
    }

    public static <T> Result<T> violations(Violation... violations) {
        return violations(Arrays.asList(violations));
    }

    public static <T> Result<T> violations(List<Violation> violations) {
        Objects.requireNonNull(violations, "violations list cannot be null");
        return new Result<>(null, Collections.unmodifiableList(violations));
    }

    /**
     * Returns {@link Result} with containing {@link Class} type of the value.
     */
    public static <T> Result<T> of(T value, List<Violation> violations) {
        return new Result<>(value, Collections.unmodifiableList(violations));
    }

    /**
     * Returns Builder of {@link Result}
     */
    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>(new ArrayList<>(), null);
    }

    public static ResultCollector combine(Result<?>... results) {
        Collection<Result<?>> collection = List.of(results);
        return new ResultCollector(collection);
    }

    /**
     * Checks if the {@link Result} is succeeded.
     * <p>
     * Returns {@code true} if {@link Result} have no violation(s), and returns {@code false} if violation is present
     */
    public boolean isSuccessful() {
        return violations.isEmpty();
    }

    /**
     * Checks if the {@link Result} is violation
     * <p>
     * Returns {@code true} if violation(s) is present, and returns {@code false} is no violation(s) is present
     */
    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    /**
     * Check if the {@link Result} containing specific violation that given to the param.
     *
     * @param violation the violation
     * @return Returns {@code true} if it's contains {@code E}, otherwise {@code false}
     */
    public boolean containsViolation(Violation violation) {
        Objects.requireNonNull(violation);
        return violations.contains(violation);
    }

    public boolean containsViolation(Predicate<Violation> predicate) {
        Objects.requireNonNull(predicate);
        return violations.stream().anyMatch(predicate);
    }

    /**
     * If {@link Result} is success then performs the given action with the reference, otherwise
     * perform nothing.
     */
    public void ifSuccess(Consumer<T> action) {
        if (isSuccessful()) action.accept(value);
    }

    /**
     * If {@link Result} is failed then performs the given action with the violation, otherwise
     * perform nothing.
     */
    public void ifViolated(Consumer<List<Violation>> action) {
        if (hasViolations()) action.accept(violations);
    }

    /**
     * If {@link Result} is success then returns {@link Stream} containing {@link T} reference.
     */
    public Stream<T> stream() {
        if (isSuccessful()) {
            return Stream.of(value);
        } else {
            return Stream.empty();
        }
    }

    /**
     * If {@link Result} is failed then returns {@link Stream} containing violations.
     */
    public Stream<Violation> streamViolations() {
        if (!isSuccessful()) {
            return violations.stream();
        } else {
            return Stream.empty();
        }
    }

    /**
     * If the {@link Result} is success then returns {@code T}, otherwise
     * process the violation(s)
     *
     * @return the value
     */
    public T get() {
        if (isSuccessful()) return value;

        process();

        throw new ViolationException(
                "Result contains violations: " + violations
        );
    }

    /**
     * If the {@link Result} is success then returns {@code T}, otherwise returns other value
     *
     * @param otherwise The other value if the {@link Result} returns null / failed
     * @return Either {@link Result} {@code T} or other value
     */
    public T orElse(T otherwise) {
        if (isSuccessful()) {
            return value;
        } else {
            Objects.requireNonNull(otherwise);

            return otherwise;
        }
    }

    /**
     * @return Returns immutable {@link List} of {@link Object} violation
     */
    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        if (hasViolations()) {
            return Result.violations(violations);
        }

        try {
            return Result.success(mapper.apply(value));
        } catch (Exception e) {
            return Result.violation(e::getMessage);
        }
    }

    public <R> Result<R> flatMap(Function<? super T, Result<R>> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        if (hasViolations()) {
            return Result.violations(violations);
        }

        try {
            return Objects.requireNonNull(mapper.apply(value));
        } catch (Exception e) {
            return Result.violations(e::getMessage);
        }
    }

    private void process() {
        if (!hasViolations()) return;

        final ViolationHandler handler = ViolationHandler.getHandler();

        if (getViolations().size() == 1) {
            handler.handle(getViolations().getFirst());
        } else {
            handler.handle(getViolations());
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "violations=" + violations +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Result<?> result = (Result<?>) object;
        return Objects.equals(violations, result.violations) && Objects.equals(value, result.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(violations, value);
    }
}
