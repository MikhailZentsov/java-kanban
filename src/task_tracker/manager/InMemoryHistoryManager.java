package task_tracker.manager;

import task_tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;

    InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (history.size() == 10) { history.remove(0); }
        if (!history.contains(task)) { history.add(task); }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
