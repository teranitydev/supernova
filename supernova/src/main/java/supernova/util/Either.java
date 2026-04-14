package supernova.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An immutable object that may contain a value reference of {@link T} succeed or a violation value which fail.
 * Each instance will either have empty {@link T} type which led to fail or empty violation {@link E} which led to succeed.
 *
 * @param <T> the type of reference result
 * @param <E> the type of violation or error
 *
 * @author Izhar Atharzi
 * @since 0.0.1
 */
public class Either<T, E> {

    private final T reference;
    private final E violation;

    /**
     * Returns {@link Either} containing succeed value reference.
     */
    public static <T, E> Either<T, E> succeed(T reference) {
        return new Either<>(reference, null);
    }

    /**
     * Returns {@link Either} containing fail violation value.
     */
    public static <T, E> Either<T, E> violation(E violation) {
        return new Either<>(null, violation);
    }

    /**
     * The default constructor of {@link Either} which only either
     * {@link T} type or {@link E} type should be present.
     *
     * @throws IllegalStateException if both types are present
     */
    private Either(T reference, E violation) {
        if (reference != null && violation != null || reference == null && violation == null)
            throw new IllegalStateException("only either one type can be present/empty");

        this.reference = reference;
        this.violation = violation;
    }

    /**
     * Checks if the result is succeeded.
     * <p>
     * Returns {@code true} if reference is present, and returns {@code false} if violation is present
     */
    public boolean isSuccess() {
        return reference != null && violation == null;
    }

    /**
     * Checks if the result is violation
     * <p>
     * Returns {@code true} if violation is present, and returns {@code false} is reference is present
     */
    public boolean isViolation() {
        return reference == null && violation != null;
    }

    /**
     * If {@link Either} is success then returns the reference, otherwise returns {@code other}.
     */
    public T orElse(T other) {
        return isSuccess() ? reference : other;
    }

    /**
     * If {@link Either} is success then returns the reference, otherwise returns the reference produced
     * by the supplying function.
     */
    public T orElse(Supplier<? extends T> supplier) {
        return isSuccess() ? reference : supplier.get();
    }

    /**
     * If {@link Either} is success then returns the reference, otherwise throw an exception
     * if the violation type is an exception.
     */
    public T orElseThrow() {
        if (isSuccess()) {
            return reference;
        } else {
            if (violation instanceof RuntimeException throwable) {
                throw throwable;
            } else if (violation instanceof Throwable throwable) {
                throw new RuntimeException(throwable);
            } else {
                throw new IllegalStateException("violation is not exception");
            }
        }
    }

    /**
     * If {@link Either} is success then performs the given action with the reference, otherwise
     * perform nothing.
     */
    public void ifSuccess(Consumer<T> action) {
        if (isSuccess()) action.accept(reference);
    }

    /**
     * If {@link Either} is failed then performs the given action with the violation, otherwise
     * perform nothing.
     */
    public void ifViolation(Consumer<E> action) {
        if (isViolation()) action.accept(violation);
    }

    /**
     * If {@link Either} is success then returns {@link Stream} containing {@link T} reference.
     */
    public Stream<T> stream() {
        if (isSuccess()) {
            return Stream.of(reference);
        } else {
            return Stream.empty();
        }
    }

    /**
     * If {@link Either} is failed then returns {@link Stream} containing {@link E} violation.
     */
    public Stream<E> streamViolation() {
        if (isSuccess()) {
            return Stream.of(violation);
        } else {
            return Stream.empty();
        }
    }

    public T getReference() {
        return reference;
    }

    public E getViolation() {
        return violation;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Either<?, ?> either = (Either<?, ?>) object;
        return Objects.equals(reference, either.reference) && Objects.equals(violation, either.violation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, violation);
    }

    @Override
    public String toString() {
        return "Either{" +
                "reference=" + reference +
                ", violation=" + violation +
                '}';
    }
}
