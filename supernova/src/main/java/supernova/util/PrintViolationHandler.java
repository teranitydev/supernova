package supernova.util;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ViolationHandler} that handle {@link String} by {@code System.out.println(string)}.
 */
public class PrintViolationHandler extends ViolationHandler {

    private String format = "Error: %s";

    @Override
    public void handle(Violation violation) {
        final String message = String.format(format, violation.getMessage());
        System.out.println(message);
    }

    @Override
    public void handle(List<Violation> violations) {
        final List<String> messages = new ArrayList<>();
        for (Violation violation : violations) {
            messages.add(violation.getMessage());
        }
        messages.forEach(System.out::println);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
