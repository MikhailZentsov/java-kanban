package task_tracker.manager.exeptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(){
        super();
    }
    public ManagerSaveException(final String message) {
        super(message);
    }
}
