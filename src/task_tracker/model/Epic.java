package task_tracker.model;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Epic extends Task {
    private final List<Integer> subtasks;
    private Instant endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
        this.endTime = this.startTime;
    }

    public Epic(String name, String description, Integer id, Status status) {
        super(name, description, id, status);
        this.subtasks = new ArrayList<>();
        this.endTime = this.startTime;
    }

    public Epic(String name, String description, Integer id, Status status, Duration duration, Instant startTime) {
        super(name, description, id, status, duration, startTime);
        this.subtasks = new ArrayList<>();
        this.endTime = this.startTime;
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public boolean addSubtask(Integer id) {
        if (!subtasks.contains(id)) {
            subtasks.add(id);

            return true;
        }

        return false;
    }

    public boolean removeSubtask(Integer id) {
        if (subtasks.contains(id)) {
            subtasks.remove(id);

            return true;
        }

        return false;
    }

    public void removeAllSubtasks() {
        this.subtasks.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;

        return subtasks.equals(epic.subtasks)
                && getName().equals(epic.getName())
                && getDescription().equals(epic.getDescription())
                && getId().equals(epic.getId())
                && getStatus().equals(epic.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus(), subtasks);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString().substring(0, super.toString().length() - 1)
                + ", subtasksID=");

        if (!subtasks.isEmpty()) {
            result.append(subtasks);
        } else {
            result.append("[Empty]");
        }

        result.append("}");

        return result.toString();
    }

    @Override
    public String toSaveString() {

        return String.valueOf(id) + ',' +
                TaskType.EPIC + ',' +
                name + ',' +
                description + ',' +
                status + ',' +
                duration + ',' +
                startTime;
    }
}
