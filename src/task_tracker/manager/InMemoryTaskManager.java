package task_tracker.manager;

import org.jetbrains.annotations.NotNull;
import task_tracker.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;


    protected final HistoryManager historyManager = new InMemoryHistoryManager();

    public InMemoryTaskManager() {
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.tasks = new HashMap<>();
        id = 1;
    }

    @Override
    public Task getAnyTaskById(int id) {
        if (tasks.containsKey(id)) { return getTask(id); }
        if (epics.containsKey(id)) { return getEpic(id); }
        if (subtasks.containsKey(id)) { return getSubtask(id); }

        return null;
    }

    @Override
    public void addTask(@NotNull Task task) {
        task.setId(id);
        tasks.put(id, task);
        id++;
    }

    @Override
    public void addEpic(@NotNull Epic task) {
        task.setId(id);
        epics.put(id, task);
        id++;
    }

    @Override
    public boolean addSubtask(Subtask task) {
        if (task != null) {
            if (epics.containsKey(task.getParentEpicId())) {
                task.setId(id);
                subtasks.put(task.getId(), task);
                epics.get(task.getParentEpicId()).addSubtask(task);
                id++;

                return true;
            }
        }

        return false;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : getEpics()) {
            epic.removeAllSubtasks();
        }

        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }

        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        }

        return null;
    }

    @Override
    public boolean updateTask(@NotNull Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);

            return true;
        }

        return false;
    }

    @Override
    public boolean updateEpic(@NotNull Epic task) {
        if (epics.containsKey(task.getId())) {
            Epic updateTask = epics.get(task.getId());

            updateTask.setName(task.getName());
            updateTask.setDescription(task.getDescription());

            return true;
        }

        return false;
    }

    @Override
    public boolean updateSubtask(@NotNull Subtask task) {
        if (subtasks.containsKey(task.getId()) && epics.containsKey(task.getParentEpicId())) {
            subtasks.put(task.getId(), task);
            epics.get(task.getParentEpicId()).setStatus();

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        boolean isDeleted = true;

        if (epics.containsKey(id)) {
            List<Subtask> tasks = new ArrayList<>(epics.get(id).getSubtasks());
            for (Subtask task : tasks) {
                if (!(deleteSubtask(task) & isDeleted)) { isDeleted = false; }
            }

            epics.remove(id);
            historyManager.remove(id);
        } else {
            isDeleted = false;
        }

        return isDeleted;
    }

    @Override
    public boolean deleteSubtask(Subtask task) {
        if (task != null) {
            if (subtasks.containsKey(task.getId())) {
                int idParent = subtasks.get(task.getId()).getParentEpicId();
                subtasks.remove(task.getId());
                epics.get(idParent).removeSubtask(task.getId());
                epics.get(idParent).setStatus();
                historyManager.remove(task.getId());

                return true;
            }
        }

        return false;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}