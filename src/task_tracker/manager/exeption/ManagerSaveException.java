package task_tracker.manager.exeption;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(){
        super();
    }
    public ManagerSaveException(final String message) {
        super(message);
    }
}
