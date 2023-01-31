package task_tracker.manager;

import task_tracker.model.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}
