package supernova.functional;

/**
 * Represent object as a warning of {@link Result} that does not affect the operation of the {@link Result} but
 * is worth enough for developer to notice.
 *
 * <p>A warning only contains a message.</p>
 *
 * @param message
 *
 * @author Izhar Atharzi
 * @since 1.1.1
 */
public record Warning(String message) {

    public static Warning of(String message) {
        return new Warning(message);
    }
}
