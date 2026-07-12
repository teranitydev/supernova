package supernova.functional;

import java.util.Objects;

/**
 * Violation represent as error that contain error field and error message.
 *
 * @author Izhar Atharzi
 * @since 1.0.0
 */
public class Violation {

    private final String code;
    private final String message;
    private final Object object;

    /**
     * Creates violation builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates violation from object only.
     */
    public static Violation of(Object object) {
        return new Violation(null, null, object);
    }

    /**
     * Creates violation from code and message.
     */
    public static Violation of(String code, String message) {
        return new Violation(code, message, null);
    }

    /**
     * Creates violation from all variables.
     */
    public static Violation of(String code, String message, Object object) {
        return new Violation(code, message, object);
    }

    /**
     * Construct all the fields.
     */
    private Violation(String code, String message, Object object) {
        this.object = object;
        this.code = Objects.requireNonNullElse(code, "");
        this.message = message;
    }

    /**
     * Gets the code of the violation.
     *
     * <p>It could be anything, e.g. "ACCOUNT_NOT_FOUND" or 404.</p>
     *
     * @return Violation code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the message of the violation.
     *
     * @return Violation message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets a nullable object of the violation that might have to hold, e.g. Exception/Throwable.
     *
     * @return Violation object.
     */
    public Object getObject() {
        return object;
    }

    /**
     * Builder for violation
     */
    public static final class Builder {

        private Object object;
        private String code;
        private String message;

        public Builder() {
        }

        public Builder object(Object object) {
            this.object = object;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Violation build() {
            return new Violation(code, message, object);
        }
    }
}
