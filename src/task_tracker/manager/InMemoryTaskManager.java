package task_tracker.manager;

import task_tracker.model.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;
    protected final Set<Task> tasksTree;
    protected final Map<Instant, Boolean> planningPeriod;
    protected final int PLANNING_PERIOD_MINUTES = 15;
    protected final int SECOND_IN_MINUTES = 60;

    public InMemoryTaskManager() {
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.tasks = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();
        this.tasksTree = new TreeSet<>((a, b) -> {
            if (a.getStartTime().isAfter(b.getStartTime())) {
                return 1;
            } else if (a.getStartTime().isBefore(b.getStartTime())) {
                return -1;
            } else {
                return 0;
            }
        });
        this.planningPeriod = new HashMap<>();
        id = 1;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksTree);
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
        tasksTree.clear();
        planningPeriod.clear();
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
            if (task.getDuration() == null) {
                task.setDuration(Duration.ZERO);
            } else {
                normalizeDuration(task);
            }
            if (task.getStartTime() == null) {
                task.setStartTime(Instant.MIN);
            } else {
                normalizeTime(task);
                if (isBusyPlanningPeriod(task)) {
                    return false;
                }
            }
            tasks.put(id, task);
            tasksTree.add(task);
            addToPlanningPeriod(task);
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
            epic.setDuration(Duration.ZERO);
            if (epic.getStartTime() == null) {
                epic.setStartTime(Instant.MIN);
            }
            epics.put(id, epic);
            tasksTree.add(epic);
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

                if (subtask.getDuration() == null) {
                    subtask.setDuration(Duration.ZERO);
                } else {
                    normalizeDuration(subtask);
                }

                if (subtask.getStartTime() == null) {
                    subtask.setStartTime(Instant.MIN);
                } else {
                    normalizeTime(subtask);

                    if (isBusyPlanningPeriod(subtask)) {
                        return false;
                    }
                }

                subtasks.put(id, subtask);
                tasksTree.add(subtask);
                addToPlanningPeriod(subtask);
                parentEpic.addSubtask(id);
                id++;

                setEpicStatus(parentEpic);
                setEpicDuration(parentEpic);
                setEpicTime(parentEpic);

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
            setEpicDuration(oldParentEpic);
            setEpicTime(oldParentEpic);

            if (!oldParentEpic.equals(newParentEpic)) {
                subtaskForUpdate.setParentEpicId(subtask.getParentEpicId());
                oldParentEpic.removeSubtask(subtask.getId());
                newParentEpic.addSubtask(subtask.getId());
                setEpicStatus(newParentEpic);
                setEpicDuration(newParentEpic);
                setEpicTime(newParentEpic);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteAnyTask(int id) {
        if (deleteTask(id)) {
            return true;
        }
        if (deleteSubtask(id)) {
            return true;
        }
        if (deleteEpic(id)) {
            return true;
        }

        return false;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id).getSubtasks().stream()
                    .map(subtasks::get)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private boolean deleteTask(int id) {
        if (tasks.containsKey(id)) {
            removeFromPlanningPeriod(tasks.get(id));
            tasksTree.remove(tasks.get(id));
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
                if (!(deleteSubtask(key) & isDeleted)) {
                    isDeleted = false;
                }
            }

            tasksTree.remove(epics.get(id));
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
            Task subtask = subtasks.get(id);

            parentEpic.removeSubtask(id);
            setEpicStatus(parentEpic);
            setEpicDuration(parentEpic);
            setEpicTime(parentEpic);
            removeFromPlanningPeriod(subtask);
            tasksTree.remove(subtask);
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

    private void setEpicDuration(Epic epic) {
        epic.setDuration(epic.getSubtasks().stream()
                .map(subtasks::get)
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus));
    }

    private void setEpicTime(Epic epic) {
        epic.setStartTime(epic.getSubtasks().stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .reduce(Instant.MAX, (a, b) -> (a.isBefore(b)) ? a : b));

        epic.setEndTime(epic.getSubtasks().stream()
                .map(subtasks::get)
                .map(Task::getEndTime)
                .reduce(Instant.MIN, (a, b) -> (a.isAfter(b)) ? a : b));
    }

    private void normalizeDuration(Task task) {
        if (task.getDuration().toMinutes() % PLANNING_PERIOD_MINUTES > 0) {
            task.setDuration(Duration.ofMinutes(
                    ((task.getDuration().toMinutes() / PLANNING_PERIOD_MINUTES) + 1 ) * PLANNING_PERIOD_MINUTES)
            );
        }
    }

    private void normalizeTime(Task task) {
        Duration delta = Duration.ofSeconds(
                task.getStartTime().getEpochSecond() % (PLANNING_PERIOD_MINUTES * SECOND_IN_MINUTES)
        );
        task.setStartTime(task.getStartTime().minus(delta));
    }

    private void addToPlanningPeriod(Task task) {
        for (Instant time = Instant.from(task.getStartTime());
             time.isBefore(task.getEndTime());
             time = time.plus(Duration.ofMinutes(PLANNING_PERIOD_MINUTES))) {
            planningPeriod.put(time, true);
        }
    }

    private void removeFromPlanningPeriod(Task task) {
        for (Instant time = Instant.from(task.getStartTime());
             time.isBefore(task.getEndTime());
             time = time.plus(Duration.ofMinutes(PLANNING_PERIOD_MINUTES))) {
            planningPeriod.remove(time);
        }
    }

    private boolean isBusyPlanningPeriod(Task task) {
        for (Instant time = task.getStartTime();
             time.isBefore(task.getEndTime());
             time = time.plus(Duration.ofMinutes(PLANNING_PERIOD_MINUTES))) {
            if (planningPeriod.containsKey(time)) return true;
        }

        return false;
    }
}