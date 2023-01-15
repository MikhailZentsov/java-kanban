package task_tracker;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class Task {
    private final String name;
    private final String description;
    private final Integer id;
    private Status status;

    protected Task(@NotNull Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.id = task.getId();
        this.status = task.getStatus();
    }

    public Task(String name, String description, Integer id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    protected String getName() {
        return name;
    }

    protected String getDescription() {
        return description;
    }

    protected Integer getId() {
        return id;
    }

    protected Status getStatus() {
        return status;
    }

    protected void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id)
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        int hash = 17;

        if (name != null) {
            hash += name.hashCode();
        }

        hash *= 31;

        if (description != null) {
            hash += description.hashCode();
        }

        hash *= 17;

        if (id != null) {
            hash += id.hashCode();
        }

        return hash;
    }

    @Override
    public String toString() {
        return "Task{"
                + "ID='" + id + '\''
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", status='" + status + '\''
                + '}';
    }
}