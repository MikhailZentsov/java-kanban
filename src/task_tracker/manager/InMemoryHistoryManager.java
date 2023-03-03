package task_tracker.manager;

import task_tracker.model.CustomLinkedList;
import task_tracker.model.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> history;

    InMemoryHistoryManager() {
        this.history = new CustomLinkedList<>();
    }

    @Override
    public void add(Task task) {
        history.add(task, task.getId());
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }
}