package task_tracker.manager.exeption;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException() {
        super();
    }

    public ManagerLoadException(final String message) {
        super(message);
    }
}
