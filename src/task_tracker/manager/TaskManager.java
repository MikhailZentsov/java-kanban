package task_tracker.manager;

import org.jetbrains.annotations.NotNull;
import task_tracker.model.Epic;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.util.List;

public interface TaskManager {
    Task getAnyTaskById(int id);
    void addTask(@NotNull Task task);
    void addEpic(@NotNull Epic task);
    boolean addSubtask(Subtask task);
    List<Task> getTasks();
    List<Epic> getEpics();
    List<Subtask> getSubtasks();
    void clearTasks();
    void clearEpics();
    void clearSubtasks();
    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtask(int id);
    boolean updateTask(@NotNull Task task);
    boolean updateEpic(@NotNull Epic task);
    boolean updateSubtask(@NotNull Subtask task);
    boolean deleteTask(int id);
    boolean deleteEpic(int id);
    boolean deleteSubtask(Subtask task);
    List<Task> getHistory();
}
