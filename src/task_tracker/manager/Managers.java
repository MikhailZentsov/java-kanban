package task_tracker.manager;

import java.nio.file.Path;

public class Managers {
    InMemoryTaskManager inMemoryTaskManager;
    TaskManager manager;

    public Managers() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    public Managers(Path path) {
        manager = new FileBackendTaskManager(path);
    }

    public TaskManager getDefault() {
        return inMemoryTaskManager;
    }

    public TaskManager getManager() {
        return manager;
    }
}