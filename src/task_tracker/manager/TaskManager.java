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

    public void addTask(int idTask, Task task) {
        tasks.put(idTask, task);
        id++;
    }

    public void addEpic(int idEpic, Epic task) {
        epics.put(idEpic, task);
        id++;
    }

    public void addSubtask(int idSubtask, int idParent, Subtask task) {
        subtasks.put(idSubtask, task);
        epics.get(idParent).addSubtask(idSubtask);
        id++;
    }

    public void showAllTasks() {
        for (Epic epic : getEpics()) {
            System.out.println(epic.toString());
            for (Subtask task : getSubtasksByEpic(epic)) {
                System.out.println(task.toString());
            }
        }

        for (Task task : getTasks()) {
            System.out.println(task.toString());
        }
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
        List<Integer> list = new ArrayList<>();

        for (Integer key: epics.keySet()) {
            list.addAll(epics.get(key).getSubtasks());
        }

        for (Integer key : list) {
            subtasks.remove(key);
        }

        epics.clear();
    }

    public void clearSubtasks() {
        List<Integer> list = new ArrayList<>();

        for (Integer key: subtasks.keySet()) {
            list.add(subtasks.get(key).getParentEpicId());
            epics.get(subtasks.get(key).getParentEpicId()).removeSubtask(key);
        }

        for (Integer key : list) {
            setEpicStatusBySubtasks(epics.get(key));
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

    public void createTask(Task task) {
        tasks.put(id++, new Task(task));
    }

    public void createEpic(Epic epic) {
        epics.put(id++, new Epic(epic));
    }

    public void createSubtask(Subtask subtask) {
        subtasks.put(id++, new Subtask(subtask));
        setEpicStatusBySubtasks(getEpicById(subtask.getParentEpicId()));
    }

    public void renewTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void renewEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        setEpicStatusBySubtasks(epic);
    }

    public void renewSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        setEpicStatusBySubtasks(getEpicById(subtask.getParentEpicId()));
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    public void deleteEpicById(Integer id) {
        for (Integer key : epics.get(id).getSubtasks()) {
            subtasks.remove(key);
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        subtasks.remove(id);
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