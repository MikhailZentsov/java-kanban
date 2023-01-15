package task_tracker.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Subtask extends Task {
    private Integer parentEpicId;

    public Subtask(String name, String description, Integer id, Status status, Integer parentEpicId) {
        super(name, description, id, status);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(@NotNull Subtask subtask) {
        super(subtask);
        this.parentEpicId = subtask.parentEpicId;
    }

    public Integer getParentEpicId() {
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
                && ((Task) this).equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 17;

        hash += ((Task) this).hashCode();

        hash *= 31;

        if (parentEpicId != null) {
            hash += parentEpicId.hashCode();
        }

        return hash;
    }

    @Override
    public String toString() {
        return "Subtask{"
                + "ID='" + getId() + '\''
                + ", name='" + getName() + '\''
                + ", description='" + getDescription() + '\''
                + ", status='" + getStatus() + '\''
                + ", parentEpicID=" + parentEpicId
                + "}";
    }
}
