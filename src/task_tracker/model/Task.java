package task_tracker.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;

    public Task(@NotNull Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.id = task.getId();
        this.status = task.getStatus();
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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