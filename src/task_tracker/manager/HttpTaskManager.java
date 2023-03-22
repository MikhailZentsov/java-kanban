package task_tracker.manager;

import com.google.gson.*;
import task_tracker.adapter.DurationAdapter;
import task_tracker.adapter.InstantAdapter;
import task_tracker.client.KVTaskClient;
import task_tracker.exeption.ManagerExchangeException;
import task_tracker.model.Epic;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackendTaskManager {
    private static String KEY_TASKS;
    private static String KEY_EPICS;
    private static String KEY_SUBTASKS;
    private static String KEY_HISTORY;
    private static KVTaskClient taskClient;
    private static Gson gson;

    private HttpTaskManager(URL url) {
        super();
        KEY_TASKS = "Tasks";
        KEY_EPICS = "Epics";
        KEY_SUBTASKS = "Subtasks";
        KEY_HISTORY = "History";
        taskClient = new KVTaskClient(url);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    @Override
    protected void save() {
        List<Integer> history = getHistory().stream().map(Task::getId).collect(Collectors.toList());

        if (tasks.isEmpty()) {
            taskClient.put(KEY_TASKS, " ");
        } else {
            taskClient.put(KEY_TASKS, gson.toJson(new ArrayList<>(tasks.values())));
        }

        if (epics.isEmpty()) {
            taskClient.put(KEY_EPICS, " ");
        } else {
            taskClient.put(KEY_EPICS, gson.toJson(new ArrayList<>(epics.values())));
        }

        if (epics.isEmpty()) {
            taskClient.put(KEY_SUBTASKS, " ");
        } else {
            taskClient.put(KEY_SUBTASKS, gson.toJson(new ArrayList<>(subtasks.values())));
        }

        if (history.isEmpty()) {
            taskClient.put(KEY_HISTORY, " ");
        } else {
            taskClient.put(KEY_HISTORY, gson.toJson(history));
        }
    }

    public static TaskManager load(URL url) {
        HttpTaskManager manager = new HttpTaskManager(url);
        JsonElement jsonElement;
        String responseBody = taskClient.load(KEY_TASKS);

        if (responseBody != null) {
            jsonElement = JsonParser.parseString(responseBody);
            if (!jsonElement.isJsonArray()) {
                throw new ManagerExchangeException("Задачи из KV-сервера не соответствуют ожидаемому");
            }
            jsonElement.getAsJsonArray().forEach(t -> {
                Task task = gson.fromJson(t.getAsJsonObject(), Task.class);
                manager.tasks.put(task.getId(), task);
                manager.tasksTree.add(task);
                manager.addToPlanningPeriod(task);
            });
        }

        responseBody = taskClient.load(KEY_EPICS);
        if (responseBody != null) {
            jsonElement = JsonParser.parseString(responseBody);
            if (!jsonElement.isJsonArray()) {
                throw new ManagerExchangeException("Эпики из KV-сервера не соответствуют ожидаемому");
            }
            jsonElement.getAsJsonArray().forEach(t -> {
                Epic epic = gson.fromJson(t.getAsJsonObject(), Epic.class);
                manager.epics.put(epic.getId(), epic);
                manager.addToPlanningPeriod(epic);
            });
        }

        responseBody = taskClient.load(KEY_SUBTASKS);
        if (responseBody != null) {
            jsonElement = JsonParser.parseString(responseBody);
            if (!jsonElement.isJsonArray()) {
                throw new ManagerExchangeException("Подзадачи из KV-сервера не соответствуют ожидаемому");
            }
            jsonElement.getAsJsonArray().forEach(t -> {
                Subtask subtask = gson.fromJson(t.getAsJsonObject(), Subtask.class);
                manager.subtasks.put(subtask.getId(), subtask);
                manager.tasksTree.add(subtask);
                manager.epics.get(subtask.getParentEpicId()).addSubtask(subtask.getId());
                manager.addToPlanningPeriod(subtask);
            });
        }

        responseBody = taskClient.load(KEY_HISTORY);
        if (responseBody != null) {
            responseBody = responseBody.substring(1, responseBody.length() - 1);
            for (String taskId : responseBody.split(",")) {
                manager.historyManager.add(manager.getAnyTaskWithoutSave(Integer.parseInt(taskId)));
            }
        }

        return manager;
    }
}
