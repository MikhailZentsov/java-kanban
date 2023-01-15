package task_tracker;

import org.jetbrains.annotations.NotNull;

import java.util.*;

class Epic extends Task {
    private final List<Integer> subtasksId;

    protected Epic(String name, String description, Integer id, Status status) {
        super(name, description, id, status);
        this.subtasksId = new ArrayList<>();
    }

    protected Epic(@NotNull Epic epic) {
        super(epic);
        this.setStatus(Status.NEW);
        this.subtasksId = epic.getSubtasks();
    }

    protected List<Integer> getSubtasks() {
        return subtasksId;
    }

    protected void addSubtasks(Integer subtaskId) {
        if (this.subtasksId.contains(subtaskId))
            System.out.println("Подзачада с ID " + subtaskId + " уже добавлена к эпику.");
        else
            this.subtasksId.add(subtaskId);
    }

    protected void setStatus(@NotNull List<Status> list) {
        Status status = Status.IN_PROGRESS;
        boolean isAllDone = true;
        boolean isAllNew = true;

        if (!list.isEmpty()) {
            for (Status item : list) {
                if (isAllDone && (item == Status.NEW || item == Status.IN_PROGRESS)) isAllDone = false;
                if (isAllNew && (item == Status.DONE || item == Status.IN_PROGRESS)) isAllNew = false;
            }
        } else {
            status = Status.NEW;
        }

        if (isAllNew) status = Status.NEW;
        if (isAllDone) status = Status.DONE;

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
