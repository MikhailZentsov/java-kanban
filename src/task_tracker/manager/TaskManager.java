package task_tracker.manager;

import org.jetbrains.annotations.NotNull;
import task_tracker.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int id;

    public TaskManager() {
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.tasks = new HashMap<>();
        id = 1;
    }

    public boolean isContainsId(int id) {
        if (tasks.containsKey(id)) return true;
        if (epics.containsKey(id)) return true;
        return subtasks.containsKey(id);
    }

    public void addTask(@NotNull Task task) {
        task.setId(id);
        tasks.put(id, task);
        id++;
    }

    public void addEpic(@NotNull Epic task) {
        task.setId(id);
        epics.put(id, task);
        id++;
    }

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

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        for (Epic epic : getEpics()) {
            epic.removeAllSubtasks();
        }

        subtasks.clear();
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public int createTask(Task task) {
        int result = id;

        task.setId(id);
        tasks.put(id++, new Task(task));

        return result;
    }

    public int createEpic(Epic epic) {
        int result = id;

        epic.setId(id);
        epics.put(id++, new Epic(epic));

        return result;
    }

    public int createSubtask(Subtask subtask) {
        int result = id;

        if (epics.containsKey(subtask.getParentEpicId())) {
            subtask.setId(id);
            subtasks.put(id++, new Subtask(subtask));
            setEpicStatusBySubtasks(getEpicById(subtask.getParentEpicId()));
        } else {
            return 0;
        }

        return result;
    }

    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);

            return true;
        } else {
            return false;
        }
    }

    public boolean updateEpic(Epic task) {
        if (epics.containsKey(task.getId())) {
            Epic updateTask = epics.get(task.getId());

            updateTask.setName(task.getName());
            updateTask.setDescription(task.getDescription());

            return true;
        } else {
            return false;
        }
    }

    public boolean updateSubtask(Subtask task) {
        if (subtasks.containsKey(task.getId()) && epics.containsKey(task.getParentEpicId())) {
            subtasks.put(task.getId(), task);
            setEpicStatusBySubtasks(getEpicById(task.getParentEpicId()));

            return true;
        } else {
            return false;
        }
    }

    public boolean deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);

            return true;
        }

        return false;
    }

    public boolean deleteEpicById(Integer id) {
        boolean isGood = true;

        if (epics.containsKey(id)) {
            for (Integer key : epics.get(id).getSubtasks()) {
                if (!(deleteSubtaskById(key) & isGood)) { isGood = false; }
            }

            epics.remove(id);
        } else {
            isGood = false;
        }

        return isGood;
    }

    public boolean deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            int idParent = subtasks.get(id).getParentEpicId();
            subtasks.remove(id);
            setEpicStatusBySubtasks(getEpicById(idParent));

            return true;
        }

        return false;
    }

    public List<Subtask> getSubtasksByEpic(@NotNull Epic epic) {
        List<Subtask> list = new ArrayList<>();

        for (Integer key : epic.getSubtasks()) {
            list.add(subtasks.get(key));
        }

        return list;
    }

    private void setEpicStatusBySubtasks(Epic epic) {
        List<Status> list = new ArrayList<>();

        for (Subtask subtask : getSubtasksByEpic(epic)) {
            list.add(subtask.getStatus());
        }

        epic.setStatus(list);
    }
}