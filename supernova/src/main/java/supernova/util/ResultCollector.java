package supernova.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResultCollector {

    private final List<Violation> violations;

    public ResultCollector() {
        this.violations = new ArrayList<>();
    }

    public ResultCollector(Collection<Result<?>> collection) {
        this.violations = new ArrayList<>();

        for (Result<?> result : collection) {
            if (result.hasViolations()) {
                this.violations.addAll(result.getViolations());
            }
        }
    }

    public ResultCollector add(Result<?> result) {
        if (result.hasViolations()) {
            this.violations.addAll(result.getViolations());
        }
        return this;
    }

    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    public Result<Void> result() {
        return violations.isEmpty()
                ? Result.success()
                : Result.violations(violations);
    }
}
