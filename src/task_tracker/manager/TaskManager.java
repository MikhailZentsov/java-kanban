package task_tracker.manager;

import org.jetbrains.annotations.NotNull;
import task_tracker.model.Epic;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.util.List;

public interface TaskManager {
    boolean isContainsId(int id);
    void addTask(@NotNull Task task);
    void addEpic(@NotNull Epic task);
    boolean addSubtask(@NotNull Subtask task);
    List<Task> getTasks();
    List<Epic> getEpics();
    List<Subtask> getSubtasks();
    void clearTasks();
    void clearEpics();
    void clearSubtasks();
    Task getTaskById(int id);
     Epic getEpicById(int id);
    Subtask getSubtaskById(int id);
    int createTask(@NotNull Task task);
    int createEpic(@NotNull Epic epic);
    int createSubtask(@NotNull Subtask subtask);
    boolean updateTask(@NotNull Task task);
    boolean updateEpic(@NotNull Epic task);
    boolean updateSubtask(@NotNull Subtask task);
    boolean deleteTaskById(int id);
    boolean deleteEpicById(int id);
    boolean deleteSubtaskById(int id);
    List<Subtask> getSubtasksByEpic(@NotNull Epic epic);
}
