package supernova.annotations;

import java.lang.annotation.*;

/**
 * Indicates that the annotated API is currently a concept and is not yet
 * considered complete or stable.
 *
 * <p>Concept APIs are under active design and may change significantly,
 * be replaced, or be removed without notice in future releases.
 *
 * <p>This annotation is intended to communicate development status only
 * and has no effect on runtime behavior.
 *
 * @author Izhar Atharzi
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Concept {
}
