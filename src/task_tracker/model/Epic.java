package task_tracker.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public Epic(@NotNull Epic epic) {
        super(epic);
        this.setStatus(Status.NEW);
        this.subtasks = epic.getSubtasks();
    }

    public Epic(String name, String description, Integer id, Status status) {
        super(name, description, id, status);
        this.subtasks = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setStatus() {
        List<Status> list = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            list.add(subtask.getStatus());
        }

        setStatus(list);
    }
    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
    }

    public boolean addSubtask(Subtask subtask) {
        if (!this.subtasks.contains(subtask)) {
            this.subtasks.add(subtask);
            setStatus();

            return true;
        }

        return false;
    }

    public void removeSubtask(Integer id) {
        if (subtasks.contains(id)) {
            this.subtasks.remove(id);
        }
    }

    public void removeAllSubtasks() {
        this.subtasks.clear();
        this.setStatus(Status.NEW);
    }

    public void setStatus(@NotNull List<Status> list) {
        Status status = Status.IN_PROGRESS;
        boolean isAllDone = true;
        boolean isAllNew = true;

        if (!list.isEmpty()) {
            for (Status item : list) {
                if (isAllDone && (item == Status.NEW || item == Status.IN_PROGRESS)) { isAllDone = false; }
                if (isAllNew && (item == Status.DONE || item == Status.IN_PROGRESS)) { isAllNew = false; }
            }

            if (isAllNew) { status = Status.NEW; }
            if (isAllDone) { status = Status.DONE; }
        } else {
            status = Status.NEW;
        }

        this.setStatus(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;

        return Objects.equals(subtasks, epic.subtasks)
                && ((Task) this).equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 17;

        hash += ((Task) this).hashCode();
        hash *= 31;

        if (subtasks != null) {
            hash += subtasks.hashCode();
        }

        return hash;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString().substring(0, super.toString().length() - 2)
                + ", subtasks=");

        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks) {
                result.append("\n\t");
                result.append(subtask.toString());
            }
        } else {
            result.append("'Empty'");
        }

        result.append("]}");

        return result.toString();
    }

    @Override
    public String toWriteString() {

        return String.valueOf(getId()) + ',' +
                TaskType.EPIC + ',' +
                getName() + ',' +
                getDescription() + ',' +
                getStatus();
    }
}
