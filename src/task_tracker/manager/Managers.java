package task_tracker.manager;

import java.net.URL;
import java.nio.file.Path;

public class Managers {
    private Managers() {}
    public static TaskManager getManager() {
        return new InMemoryTaskManager();
    }
    public static TaskManager getManager(Path path) {
        return FileBackendTaskManager.load(path);
    }
    public static HistoryManager getHistoryManager() { return new InMemoryHistoryManager(); }
    public static TaskManager getManager(URL url){ return HttpTaskManager.load(url); }
}