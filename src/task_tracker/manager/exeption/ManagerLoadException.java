package task_tracker.manager.exeption;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(final String message) {
        super(message);
    }
}
