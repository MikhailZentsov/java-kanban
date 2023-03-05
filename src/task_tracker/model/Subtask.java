package task_tracker.model;

import java.util.Objects;

public class Subtask extends Task {
    private int parentEpicId;

    public Subtask(String name, String description, int parentEpicId) {
        super(name, description, 0, Status.NEW);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(String name, String description, Integer id, Status status, int parentEpicId) {
        super(name, description, id, status);
        this.parentEpicId = parentEpicId;
    }

    public int getParentEpicId() {
        return parentEpicId;
    }

    public void setParentEpicId(int parentEpicId) {
        this.parentEpicId = parentEpicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;

        return Objects.equals(parentEpicId, subtask.parentEpicId)
                && Objects.equals(getName(), subtask.getName())
                && Objects.equals(getDescription(), subtask.getDescription())
                && Objects.equals(getId(), subtask.getId())
                && Objects.equals(getStatus(), subtask.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus(), parentEpicId);
    }

    @Override
    public String toString() {

        return super.toString().substring(0, super.toString().length() - 1) +
                ", parentEpicID='" + parentEpicId +
                "'}";
    }

    @Override
    public String toSaveString() {

        return String.valueOf(getId()) + ',' +
                TaskType.SUBTASK + ',' +
                getName() + ',' +
                getDescription() + ',' +
                getStatus() + ',' +
                parentEpicId;
    }
}
