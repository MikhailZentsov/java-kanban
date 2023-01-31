package task_tracker.manager;

import org.jetbrains.annotations.NotNull;
import task_tracker.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int id;

    private final HistoryManager historyManager = new InMemoryHistoryManager();

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
    public boolean addSubtask(@NotNull Subtask task) {
        if (epics.containsKey(task.getParentEpicId())) {
            task.setId(id);
            subtasks.put(task.getId(), task);
            epics.get(task.getParentEpicId()).addSubtask(task.getId());
            id++;
            return true;
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
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public boolean updateTask(@NotNull Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateEpic(@NotNull Epic task) {
        if (epics.containsKey(task.getId())) {
            Epic updateTask = epics.get(task.getId());

            updateTask.setName(task.getName());
            updateTask.setDescription(task.getDescription());

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateSubtask(@NotNull Subtask task) {
        if (subtasks.containsKey(task.getId()) && epics.containsKey(task.getParentEpicId())) {
            subtasks.put(task.getId(), task);
            setEpicStatusBySubtasks(epics.get(task.getParentEpicId()));

            return true;
        } else {
            return false;
        }
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
            List<Integer> keys = new ArrayList<>(epics.get(id).getSubtasks());
            for (Integer key : keys) {
                if (!(deleteSubtask(key) & isDeleted)) { isDeleted = false; }
            }

            epics.remove(id);
            historyManager.remove(id);
        } else {
            isDeleted = false;
        }

        return isDeleted;
    }

    @Override
    public boolean deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int idParent = subtasks.get(id).getParentEpicId();
            subtasks.remove(id);
            epics.get(idParent).removeSubtask(id);
            setEpicStatusBySubtasks(epics.get(idParent));
            historyManager.remove(id);

            return true;
        }

        return false;
    }

    @Override
    public List<Subtask> getSubtasksByEpic(@NotNull Epic epic) {
        List<Subtask> list = new ArrayList<>();

        for (Integer key : epic.getSubtasks()) {
            list.add(subtasks.get(key));
        }

        return list;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void setEpicStatusBySubtasks(Epic epic) {
        List<Status> list = new ArrayList<>();

        for (Subtask subtask : getSubtasksByEpic(epic)) {
            list.add(subtask.getStatus());
        }

        epic.setStatus(list);
    }
}