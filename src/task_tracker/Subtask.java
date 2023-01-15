package task_tracker;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class Subtask extends Task {
    private final Integer parentEpicId;

    protected Subtask(String name, String description, Integer id, Status status, Integer parentEpicId) {
        super(name, description, id, status);
        this.parentEpicId = parentEpicId;
    }

    protected Subtask(@NotNull Subtask subtask) {
        super(subtask);
        this.parentEpicId = subtask.parentEpicId;
    }

    protected Integer getParentEpicId() {
        return parentEpicId;
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