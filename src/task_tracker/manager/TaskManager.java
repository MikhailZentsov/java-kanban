package task_tracker.manager;

import task_tracker.model.Epic;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getPrioritizedTasks();
    List<Task> getTasks();
    void deleteTasks();
    Task getTask(int id);
    boolean addTask(Task task);
    boolean addEpic(Epic task);
    boolean addSubtask(Subtask task);
    boolean updateTask(Task task);
    boolean updateEpic(Epic task);
    boolean updateSubtask(Subtask task);
    boolean deleteAnyTask(int id);
    List<Subtask> getEpicSubtasks(int id);
    List<Task> getHistory();
}
