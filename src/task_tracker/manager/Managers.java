package task_tracker.manager;

import task_tracker.server.HttpTaskServer;
import task_tracker.server.KVServer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class Managers {
    private Managers() {}
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static TaskManager getDefault(Path path) {
        return FileBackendTaskManager.load(path);
    }
    public static HistoryManager getHistoryManager() { return new InMemoryHistoryManager(); }
    public static TaskManager getDefault(URL url) { return HttpTaskManager.load(url); }
    public static KVServer getDefaultKVServer(String hostname, int port) throws IOException {
        return new KVServer(hostname, port);
    }
    public static HttpTaskServer getDefaultHttpTaskServer(HttpTaskManager manager, String hostname, int port) throws IOException {
        return new HttpTaskServer(manager, hostname, port);
    }
}