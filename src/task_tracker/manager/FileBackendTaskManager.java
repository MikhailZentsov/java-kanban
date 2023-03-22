package task_tracker.manager;

import org.jetbrains.annotations.NotNull;
import task_tracker.exeption.ManagerLoadException;
import task_tracker.exeption.ManagerSaveException;
import task_tracker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackendTaskManager extends InMemoryTaskManager {
    private final Path path;
    public FileBackendTaskManager() { this.path = null; }
    public FileBackendTaskManager(@NotNull Path path) {
        this.path = path;
    }

    protected void save() {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось создать файл");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(path.toAbsolutePath().toString(), StandardCharsets.UTF_8))) {

            bw.write("id,type,name,description,status,duration,start_time,parent_epic_id");

            if (!getTasks().isEmpty()) {

                bw.newLine();

                for (Task task : getTasks()) {
                    bw.write(task.toSaveString());
                    bw.newLine();
                }

                bw.newLine();

                bw.write(historyManager.getHistory()
                        .stream()
                        .map(Task::getId)
                        .map(Object::toString)
                        .collect(Collectors.joining(",")));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать файл");
        }
    }

    public static TaskManager load(Path path) {
        FileBackendTaskManager manager = new FileBackendTaskManager(path);

        if (Files.exists(path)) {
            try (BufferedReader br = new BufferedReader(
                    new FileReader(path.toAbsolutePath().toString(), StandardCharsets.UTF_8))) {
                List<String> listOfStringTasks = new ArrayList<>();
                String history;
                String line;
                boolean isTask = true;

                if (br.readLine() == null) {
                    throw new ManagerLoadException("Файл пуст");
                }

                while (br.ready() && isTask) {
                    line = br.readLine();

                    if (!line.isEmpty()) {
                        listOfStringTasks.add(line);
                    } else {
                        isTask = false;
                    }
                }

                history = br.readLine();

                createTasks(listOfStringTasks, manager);

                if (history != null) {
                    for (String taskId : history.split(",")) {
                        manager.historyManager.add(manager.getAnyTaskWithoutSave(Integer.parseInt(taskId)));
                    }
                }
            } catch (IOException | ArrayIndexOutOfBoundsException | ArrayStoreException e) {
                throw new ManagerLoadException("Файл не удалось считать");
            }
        } else {
            throw new ManagerLoadException("Файла загрузки не существует");
        }

        return manager;
    }

    protected static void createTasks(List<String> list, FileBackendTaskManager manager) {
        int maxId = 1;

        for (String item : list) {
            String[] line = item.split(",");
            switch (TaskType.valueOf(line[1])) {
                case TASK:
                    Task task = new Task(line[2],
                            line[3],
                            Integer.parseInt(line[0]),
                            Status.valueOf(line[4]),
                            Duration.ofMinutes(Long.parseLong(line[5])),
                            Instant.ofEpochSecond(Long.parseLong(line[6])));

                    if (maxId < task.getId()) {
                        maxId = task.getId();
                    }

                    manager.tasks.put(task.getId(), task);
                    manager.tasksTree.add(task);
                    manager.addToPlanningPeriod(task);
                    break;

                case EPIC:
                    Epic epic = new Epic(line[2],
                            line[3],
                            Integer.parseInt(line[0]),
                            Status.valueOf(line[4]),
                            Duration.ofMinutes(Long.parseLong(line[5])),
                            Instant.ofEpochSecond(Long.parseLong(line[6])));

                    if (maxId < epic.getId()) {
                        maxId = epic.getId();
                    }

                    manager.epics.put(epic.getId(), epic);
                    manager.addToPlanningPeriod(epic);
                    break;

                case SUBTASK:
                    Subtask subtask = new Subtask(line[2],
                            line[3],
                            Integer.parseInt(line[0]),
                            Status.valueOf(line[4]),
                            Duration.ofMinutes(Long.parseLong(line[5])),
                            Instant.ofEpochSecond(Long.parseLong(line[6])),
                            Integer.parseInt(line[7]));

                    if (maxId < subtask.getId()) {
                        maxId = subtask.getId();
                    }

                    manager.subtasks.put(subtask.getId(), subtask);
                    manager.epics.get(subtask.getParentEpicId()).addSubtask(subtask.getId());
                    manager.tasksTree.add(subtask);
                    manager.addToPlanningPeriod(subtask);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + line[1]);
            }
        }

        manager.id = ++maxId;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task result = super.getTask(id);
        save();

        return result;
    }

    @Override
    public boolean addTask(Task task) {
        boolean result = super.addTask(task);
        save();

        return result;
    }

    @Override
    public boolean addEpic(Epic epic) {
        boolean result = super.addEpic(epic);
        save();

        return result;
    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        boolean result = super.addSubtask(subtask);
        save();

        return result;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();

        return result;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean result = super.updateEpic(epic);
        save();

        return result;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean result = super.updateSubtask(subtask);
        save();

        return result;
    }

    @Override
    public boolean deleteAnyTask(int id) {
        boolean result = super.deleteAnyTask(id);
        save();

        return result;
    }

    Task getAnyTaskWithoutSave(int id) {
        return super.getTask(id);
    }
}