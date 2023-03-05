package task_tracker.manager;

import task_tracker.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.tasks = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();
        id = 1;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        list.addAll(tasks.values());
        list.addAll(epics.values());
        list.addAll(subtasks.values());

        return list;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        historyManager.clear();
    }

    @Override
    public Task getAnyTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));

            return tasks.get(id);
        }

        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));

            return epics.get(id);
        }

        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));


            return subtasks.get(id);
        }

        return null;
    }

    @Override
    public boolean addTask(Task task) {
        if (task != null) {
            task.setId(id);
            task.setStatus(Status.NEW);
            tasks.put(id, task);
            id++;

            return true;
        }

        return false;
    }

    @Override
    public boolean addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(id);

            epic.setStatus(Status.NEW);
            epic.removeAllSubtasks();
            epics.put(id, epic);
            id++;

            return true;
        }

        return false;
    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        if (subtask != null) {
            if (epics.containsKey(subtask.getParentEpicId())) {
                Epic parentEpic = epics.get(subtask.getParentEpicId());

                subtask.setId(id);
                subtask.setStatus(Status.NEW);
                subtasks.put(id, subtask);
                parentEpic.addSubtask(id);
                id++;

                setEpicStatus(parentEpic);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {

            Task taskForUpdate = tasks.get(task.getId());

            taskForUpdate.setName(task.getName());
            taskForUpdate.setDescription(task.getDescription());
            taskForUpdate.setStatus(task.getStatus());

            return true;
        }

        return false;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Epic epicForUpdate = epics.get(epic.getId());

            epicForUpdate.setName(epic.getName());
            epicForUpdate.setDescription(epic.getDescription());

            return true;
        }

        return false;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getParentEpicId())) {
            Subtask subtaskForUpdate = subtasks.get(subtask.getId());
            Epic oldParentEpic = epics.get(subtasks.get(subtask.getId()).getParentEpicId());
            Epic newParentEpic = epics.get(subtask.getParentEpicId());

            subtaskForUpdate.setName(subtask.getName());
            subtaskForUpdate.setDescription(subtask.getDescription());
            subtaskForUpdate.setStatus(subtask.getStatus());

            setEpicStatus(oldParentEpic);

            if (!oldParentEpic.equals(newParentEpic)) {
                subtaskForUpdate.setParentEpicId(subtask.getParentEpicId());
                oldParentEpic.removeSubtask(subtask.getId());
                newParentEpic.addSubtask(subtask.getId());
                setEpicStatus(newParentEpic);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteAnyTask(int id) {
        if (deleteTask(id)) { return true; }
        if (deleteSubtask(id)) { return true; }
        if (deleteEpic(id)) { return true; }

        return false;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        List<Subtask> list = new ArrayList<>();

        if (epics.containsKey(id)) {
            list = epics.get(id).getSubtasks().stream()
                    .map(subtasks::get)
                    .collect(Collectors.toList());
        }

        return list;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private boolean deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);

            return true;
        }

        return false;
    }

    private boolean deleteEpic(int id) {
        boolean isDeleted = true;

        if (epics.containsKey(id)) {
            for (Integer key : epics.get(id).getSubtasks().toArray(new Integer[0])) {
                if (!(deleteSubtask(key) & isDeleted)) { isDeleted = false; }
            }

            epics.remove(id);
            historyManager.remove(id);
        } else {
            isDeleted = false;
        }

        return isDeleted;
    }

    private boolean deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Epic parentEpic = epics.get(subtasks.get(id).getParentEpicId());

            parentEpic.removeSubtask(id);
            setEpicStatus(parentEpic);
            subtasks.remove(id);
            historyManager.remove(id);

            return true;
        }

        return false;
    }

    private void setEpicStatus(Epic epic) {
        Status status = Status.IN_PROGRESS;
        boolean isAllDone = true;
        boolean isAllNew = true;
        List<Status> list = epic.getSubtasks().stream()
                .map(subtasks::get)
                .map(Task::getStatus)
                .collect(Collectors.toList());

        if (!list.isEmpty()) {
            for (Status item : list) {
                if (isAllDone && (item == Status.NEW || item == Status.IN_PROGRESS)) {
                    isAllDone = false;
                }
                if (isAllNew && (item == Status.DONE || item == Status.IN_PROGRESS)) {
                    isAllNew = false;
                }
            }

            if (isAllNew) {
                status = Status.NEW;
            }
            if (isAllDone) {
                status = Status.DONE;
            }
        } else {
            status = Status.NEW;
        }

        epic.setStatus(status);
    }
}