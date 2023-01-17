package task_tracker.manager;

public class Managers {
    InMemoryTaskManager inMemoryTaskManager;

    public Managers() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }
    public TaskManager getDefault() {
        return inMemoryTaskManager;
    }
}
