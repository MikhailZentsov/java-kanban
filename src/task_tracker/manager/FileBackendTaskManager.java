package task_tracker.manager;

import org.jetbrains.annotations.NotNull;
import task_tracker.manager.exeption.ManagerLoadException;
import task_tracker.manager.exeption.ManagerSaveException;
import task_tracker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackendTaskManager extends InMemoryTaskManager {
    Path path;

    public FileBackendTaskManager(@NotNull Path path) {
        this.path = path;
    }

    private void save() {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        }

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(path.toAbsolutePath().toString(), StandardCharsets.UTF_8))) {
            List<Task> listTasks = new ArrayList<>();
            List<Task> listHistory = historyManager.getHistory();
            List<String> listHistoryId = new ArrayList<>();

            listTasks.addAll(getTasks());
            listTasks.addAll(getEpics());
            listTasks.addAll(getSubtasks());

            bw.write("id,type,name,description,status,parent_epic_id\n");

            for (Task task : listTasks) {
                bw.write(task.toSaveString());
                bw.newLine();
            }

            bw.write('\n');

            for (Task item : listHistory) {
                listHistoryId.add(item.getId().toString());
            }

            bw.write(String.join(",", listHistoryId.toArray(new String[0])));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackendTaskManager load(Path path) {
        FileBackendTaskManager manager = new FileBackendTaskManager(path);

        if (Files.exists(path)) {
            try (BufferedReader br = new BufferedReader(
                    new FileReader(path.toAbsolutePath().toString(), StandardCharsets.UTF_8))) {
                List<String> list = new ArrayList<>();
                String history;
                String line;
                boolean isTask = true;

                br.readLine();

                while (br.ready() && isTask) {
                    line = br.readLine();

                    if (!line.isEmpty()) {
                        list.add(line);
                    } else {
                        isTask = false;
                    }
                }

                history = br.readLine();

                createTasks(list, manager);

                if (history != null) {
                    for (String taskId : history.split(",")) {
                        manager.historyManager.add(manager.getAnyTaskById(Integer.parseInt(taskId)));
                    }
                }
            } catch (IOException e) {
                throw new ManagerLoadException(e.getMessage());
            }
        } else {
            throw new ManagerLoadException("Файла загрузки не существует.");
        }

        return manager;
    }

    private static void createTasks(List<String> list, FileBackendTaskManager manager) {
        int maxId = 1;

        for (String item : list) {
            String[] line = item.split(",");
            switch (TaskType.valueOf(line[1])) {
                case TASK:
                    Task task = new Task(line[2], line[3], Integer.parseInt(line[0]), Status.valueOf(line[4]));

                    if (maxId < task.getId()) {
                        maxId = task.getId();
                    }

                    manager.tasks.put(task.getId(), task);
                    break;

                case EPIC:
                    Epic epic = new Epic(line[2], line[3], Integer.parseInt(line[0]), Status.valueOf(line[4]));

                    if (maxId < epic.getId()) {
                        maxId = epic.getId();
                    }

                    manager.epics.put(epic.getId(), epic);
                    break;

                case SUBTASK:
                    Subtask subtask = new Subtask(line[2],
                            line[3],
                            Integer.parseInt(line[0]),
                            Status.valueOf(line[4]),
                            Integer.parseInt(line[5]));

                    if (maxId < subtask.getId()) {
                        maxId = subtask.getId();
                    }

                    manager.subtasks.put(subtask.getId(), subtask);
                    manager.epics.get(subtask.getParentEpicId()).addSubtask(subtask);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + line[1]);
            }
        }

        manager.id = maxId;
    }

    @Override
    public void addTask(@NotNull Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(@NotNull Epic task) {
        super.addEpic(task);
        save();
    }

    @Override
    public boolean addSubtask(Subtask task) {
        boolean result = super.addSubtask(task);
        save();
        return result;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public boolean updateTask(@NotNull Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateEpic(@NotNull Epic task) {
        boolean result = super.updateEpic(task);
        save();
        return result;
    }

    @Override
    public boolean updateSubtask(@NotNull Subtask task) {
        return super.updateSubtask(task);
    }

    @Override
    public boolean deleteTask(int id) {
        boolean result = super.deleteTask(id);
        save();
        return result;
    }

    @Override
    public boolean deleteEpic(int id) {
        boolean result = super.deleteEpic(id);
        save();
        return result;
    }

    @Override
    public boolean deleteSubtask(Subtask task) {
        boolean result = super.deleteSubtask(task);
        save();
        return result;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic task = super.getEpic(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask task = super.getSubtask(id);
        save();
        return task;
    }
}