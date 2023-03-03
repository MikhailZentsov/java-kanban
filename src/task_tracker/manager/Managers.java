package task_tracker.manager;

import java.nio.file.Path;

public class Managers {
    TaskManager manager;

    public Managers() {
        manager = new InMemoryTaskManager();
    }

    public Managers(Path path) {
        manager = FileBackendTaskManager.load(path);
    }

    public TaskManager getManager() {
        return manager;
    }
}