package task_tracker.exeption;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(final String message) {
        super(message);
    }
}
