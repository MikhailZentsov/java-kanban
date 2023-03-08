package task_tracker.manager;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getManagers(Path path) {
        return FileBackendTaskManager.load(path);
    }
}