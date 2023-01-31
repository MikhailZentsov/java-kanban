package task_tracker.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Epic extends Task {
    private final List<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(@NotNull Epic epic) {
        super(epic);
        this.setStatus(Status.NEW);
        this.subtasksId = epic.getSubtasks();
    }

    public List<Integer> getSubtasks() {
        return subtasksId;
    }

    public void addSubtask(Integer subtaskId) {
        if (this.subtasksId.contains(subtaskId))
            System.out.println("Подзачада с ID " + subtaskId + " уже добавлена к эпику.");
        else
            this.subtasksId.add(subtaskId);
    }

    public void removeSubtask(Integer id) {
        this.subtasksId.remove(id);
    }

    public void removeAllSubtasks() {
        this.subtasksId.clear();
        this.setStatus(Status.NEW);
    }

    public void setStatus(@NotNull List<Status> list) {
        Status status = Status.IN_PROGRESS;
        boolean isAllDone = true;
        boolean isAllNew = true;

        if (!list.isEmpty()) {
            for (Status item : list) {
                if (isAllDone && (item == Status.NEW || item == Status.IN_PROGRESS)) isAllDone = false;
                if (isAllNew && (item == Status.DONE || item == Status.IN_PROGRESS)) isAllNew = false;
            }

            if (isAllNew) status = Status.NEW;
            if (isAllDone) status = Status.DONE;
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
        return Objects.equals(subtasksId, epic.subtasksId)
                && ((Task) this).equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 17;

        hash += ((Task) this).hashCode();

        hash *= 31;

        if (subtasksId != null) {
            hash += subtasksId.hashCode();
        }

        return hash;
    }

    @Override
    public String toString() {
        String result = "Epic{"
                + "ID='" + getId() + '\''
                + ", name='" + getName() + '\''
                + ", description='" + getDescription() + '\''
                + ", status='" + getStatus() + '\''
                + ", subtasksID=";

        if (subtasksId != null) {
            result += subtasksId.toString();
        }

        result += "}";

        return result;
    }
}
