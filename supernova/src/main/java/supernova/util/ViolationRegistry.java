package supernova.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A STATIC container that store {@link ViolationHandler} for each {@link Class}.
 */
public class ViolationRegistry {

    private static final Map<Class<?>, ViolationHandler<?>> HANDLERS = new ConcurrentHashMap<>();

    // Supernova Default Violation Handlers
    static {
        HANDLERS.put(String.class, new PrintViolationHandler());
        HANDLERS.put(Exception.class, new ExceptionViolationHandler());
        HANDLERS.put(Throwable.class, new ThrowableViolationHandler());
    }

    public static void addHandler(Class<?> classType, ViolationHandler<?> violationHandler) {
        if (HANDLERS.containsKey(classType)) return;

        HANDLERS.put(classType, violationHandler);
    }

    public static void removeHandler(Class<?> classType, ViolationHandler<?> violationHandler) {
        if (!HANDLERS.containsKey(classType)) return;
        if (!HANDLERS.get(classType).equals(violationHandler)) return;

        HANDLERS.remove(classType, violationHandler);
    }

    public static ViolationHandler<?> getViolationHandler(Class<?> violationClass) {
        ViolationHandler<?> handler = HANDLERS.get(violationClass);
        if (handler != null) return handler;

        if (Exception.class.isAssignableFrom(violationClass)) {
            handler = HANDLERS.get(Exception.class);
        } else if (Throwable.class.isAssignableFrom(violationClass)) {
            handler = HANDLERS.get(Throwable.class);
        }

        if (handler != null) {
            HANDLERS.put(violationClass, handler);
        }

        return handler;
    }
}
