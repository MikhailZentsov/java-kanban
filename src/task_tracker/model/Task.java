package task_tracker.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Integer id;
    protected Status status;
    protected Duration duration;
    protected Instant startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = Status.NEW;
        this.duration = Duration.ZERO;
        this.startTime = Instant.MIN;
    }

    public Task(String name, String description, Integer id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = Duration.ZERO;
        this.startTime = Instant.MIN;
    }

    public Task(String name, String description, Integer id, Status status, Duration duration, Instant startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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
    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    public Instant getStartTime() {
        return startTime;
    }
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    public Instant getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;

        return id.equals(task.id)
                && name.equals(task.name)
                && description.equals(task.description)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {

        return this.getClass().getName() +
                '{' +
                "ID='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                '}';
    }

    public String toSaveString() {

        return String.valueOf(id) + ',' +
                TaskType.TASK + ',' +
                name + ',' +
                description + ',' +
                status + ',' +
                duration + ',' +
                startTime;
    }
}