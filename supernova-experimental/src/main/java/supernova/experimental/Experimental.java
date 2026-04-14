package supernova.experimental;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Experimental {
    String value() default "";
}
