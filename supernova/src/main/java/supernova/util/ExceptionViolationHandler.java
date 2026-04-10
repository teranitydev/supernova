package supernova.util;

import java.util.ArrayList;
import java.util.List;

public class ExceptionViolationHandler extends ViolationHandler {

    @Override
    public void handle(Violation violation) {
        throw new ViolationException(violation.getMessage());
    }

    @Override
    public void handle(List<Violation> violations) {
        List<String> messages = new ArrayList<>();
        for (Violation violation : violations) {
            messages.add(violation.getMessage());
        }
        throw new ViolationException(messages.toString());
    }
}
