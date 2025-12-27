package supernova.util;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
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
 * <p>If {@code T} is present it means {@link Result} of an operation is succeeded,
 * otherwise, if {@link Result} have violation(s) it means operation is failed.
 *
 * <p>Success {@link Result} would require NO violations at all.
 *
 * <p>Violation(s) or also called error(s) can be {@link String} of error message, {@link Exception}, etc.
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
    private final List<Violation<?>> violations;

    /**
     * The instance of the value reference.
     */
    private final T value;

    /**
     * The default constructor of {@link Result}.
     *
     * @param value the type of the value reference
     */
    private Result(T value, List<Violation<?>> violations) {
        this.value = value;
        this.violations = violations;
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
    public static <T, E> Result<T> violation(E violation) {
        return violations(List.of(violation));
    }

    @SafeVarargs
    public static <T, E> Result<T> violations(E... violations) {
        return violations(Arrays.asList(violations));
    }

    public static <T, E> Result<T> violations(List<E> violations) {
        Objects.requireNonNull(violations, "violations list cannot be null");

        final List<Violation<?>> violationsList = new ArrayList<>();
        for (E violation : violations) {
            violationsList.add(new Violation<>(violation));
        }

        return new Result<>(null, Collections.unmodifiableList(violationsList));
    }

    /**
     * Returns {@link Result} with containing {@link Class} type of the value.
     */
    public static <T> Result<T> of(T value, List<Violation<?>> violations) {
        return new Result<>(value, Collections.unmodifiableList(violations));
    }

    /**
     * Returns Builder of {@link Result}
     */
    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>(new CopyOnWriteArrayList<>(), null);
    }

    /**
     * Checks if the {@link Result} is succeeded.
     * <p>
     * Returns {@code true} if reference is present, and returns {@code false} if violation is present
     */
    public boolean isSuccessful() {
        return violations.isEmpty() && value != null;
    }

    /**
     * Checks if the {@link Result} is violation
     * <p>
     * Returns {@code true} if violation is present, and returns {@code false} is reference is present
     */
    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    /**
     * Check if the {@link Result} containing specific violation that given to the param.
     *
     * @param violationValue the type of the violation value
     * @return Returns {@code true} if it's contains {@code E}, otherwise {@code false}
     */
    public boolean containsViolation(Object violationValue) {
        Objects.requireNonNull(violationValue);

        if (violations.isEmpty()) return false;

        return violations.stream().anyMatch(violation -> violation.value().equals(violationValue));
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
    public void ifViolated(Consumer<List<Violation<?>>> action) {
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
    public Stream<Violation<?>> streamViolations() {
        if (!isSuccessful()) {
            return violations.stream();
        } else {
            return Stream.empty();
        }
    }

    /**
     * If the {@link Result} is success then returns {@code T}, otherwise violation(s).
     *
     * @return the value
     */
    public T get() {
        if (!isSuccessful()) {
            for (Violation<?> violation : violations) {
                handleViolation(violation);
            }

            return null;
        }
        return value;
    }

    /**
     * If the {@link Result} is success the returns {@code T}, otherwise returns other value
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
    public List<Violation<?>> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    @SuppressWarnings("unchecked")
    private <V> void handleViolation(Violation<V> violation) {
        final ViolationHandler<V> violationHandler =
                (ViolationHandler<V>) ViolationHandlers.getViolationHandler(violation.value().getClass());

        if (violationHandler != null) {
            violationHandler.handle(violation);
        } else {
            throw new ViolationHandlerNotFoundException("Can't find violation handler for class type: " + violation.value().getClass());
        }
    }

    /**
     * Handles all violations.
     */
    public void handleViolations() {
        for (Violation<?> violation : violations) {
            handleViolation(violation);
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
