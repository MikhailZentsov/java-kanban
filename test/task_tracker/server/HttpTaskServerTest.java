package task_tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import task_tracker.adapter.DurationAdapter;
import task_tracker.adapter.InstantAdapter;
import task_tracker.manager.HttpTaskManager;
import task_tracker.manager.InMemoryTaskManager;
import task_tracker.manager.Managers;
import task_tracker.model.Epic;
import task_tracker.model.Status;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static HttpTaskManager manager;
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private static HttpClient client;
    private static Gson gson;
    private static Task task1;
    private static Task task2;
    private static Task task3;
    private static Epic epic1;
    private static Epic epic2;
    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer("localhost", 8084);
        kvServer.start();
        manager = (HttpTaskManager) Managers.getDefault(new URL("http://localhost:8084"));
        taskServer = new HttpTaskServer(manager, "localhost", 8082);
        taskServer.start();
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }
    @AfterAll
    static void afterAll() {
        taskServer.stop(0);
        kvServer.stop(0);
    }
    @AfterEach
    void afterEach() {
        manager.deleteTasks();
    }

    @BeforeEach
    void setUp() {
        task1 = new Task(
                "name.task1",
                "description.task1",
                1,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES * 2),
                Instant.MIN);
        task2 = new Task(
                "name.task2",
                "description.task2",
                2,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES * 2),
                Instant.ofEpochSecond(1678309200));
        task3 = new Task(
                "name.task3",
                "description.task3",
                3,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES * 2),
                Instant.ofEpochSecond(1678136400));
        epic1 = new Epic("name.epic1", "description.epic1", 4, Status.NEW);
        epic2 = new Epic("name.epic2", "description.epic2", 5, Status.NEW);
        subtask1 = new Subtask(
                "name.subtask1",
                "description.subtask1",
                6,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES),
                Instant.ofEpochSecond(1679136400),
                4);
        subtask2 = new Subtask(
                "name.subtask2",
                "description.subtask2",
                7,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES),
                Instant.ofEpochSecond(1689136400),
                4);
    }

    @Test
    void testCorrectGetPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(manager.getPrioritizedTasks().toString(),
                response.body(),
                "Данные пустого менеджера не совпадают");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        List<String> list = new ArrayList<>();
        list.add(gson.toJson(task3));
        list.add(gson.toJson(task2));
        list.add(gson.toJson(task1));

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(list.toString().replace(" ",""),
                response.body(),
                "Вывод getPrioritized не совпадают");
    }

    @Test
    void testCorrectGetTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(String.valueOf(manager.getTasks()),
                response.body(),
                "Данные пустого менеджера не совпадают");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(epic1);
        manager.addTask(epic2);
        manager.addTask(subtask1);
        manager.addTask(subtask2);

        List<String> list = new ArrayList<>();
        list.add(gson.toJson(task1));
        list.add(gson.toJson(task2));
        list.add(gson.toJson(task3));
        list.add(gson.toJson(epic1));
        list.add(gson.toJson(epic2));
        list.add(gson.toJson(subtask1));
        list.add(gson.toJson(subtask2));

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(list.toString().replace(" ",""),
                response.body(),
                "Вывод getTasks не совпадают");
    }

    @Test
    void testCorrectGetTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(String.valueOf(manager.getTask(1)),
                response.body(),
                "Данные пустого менеджера не совпадают");

        manager.addTask(task1);
        manager.addTask(task2);

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(gson.toJson(task1), response.body(),
                "Получение по ID работает не корректно для существующей задачи");
    }

    @Test
    void testCorrectGetEpicSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/subtasks/epic/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        List<String> list = new ArrayList<>();
        list.add(gson.toJson(subtask1));
        list.add(gson.toJson(subtask2));

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(list.toString().replace(" ",""),
                response.body(),
                "Вывод getEpicSubtasks не совпадают для эпика с подзадачами");

        url = URI.create("http://localhost:8082/tasks/subtasks/epic/?id=5");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(new ArrayList<>().toString(),
                response.body(),
                "Вывод getEpicSubtasks не совпадают для эпика без подзадач");
    }

    @Test
    void testCorrectGetHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(String.valueOf(manager.getHistory()),
                response.body(),
                "Данные пустого менеджера истории не совпадают");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(String.valueOf(manager.getHistory()),
                response.body(),
                "Данные пустого менеджера истории не совпадают после добавления подзадач");

        manager.getTask(1);
        manager.getTask(4);
        manager.getTask(7);
        manager.getTask(8);
        manager.getTask(1);

        List<String> list = new ArrayList<>();
        list.add(gson.toJson(epic1));
        list.add(gson.toJson(subtask2));
        list.add(gson.toJson(task1));

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(list.toString().replace(" ",""),
                response.body(),
                "Данные менеджера истории не совпадают после добавления подзадач");
    }

    @Test
    void testDeleteTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.getTask(1);
        manager.getTask(4);
        manager.getTask(7);
        manager.getTask(8);
        manager.getTask(1);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Все задачи удалены",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(200,
                response.statusCode(),
                "Код ответа не совпадает");
    }

    @Test
    void testCorrectDeleteTaskByID() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        manager.addTask(task1);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Задача удалена",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(201,
                response.statusCode(),
                "Код ответа не совпадает");

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Задача не найдена",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(404,
                response.statusCode(),
                "Код ответа не совпадает");

        url = URI.create("http://localhost:8082/tasks/task/?id=sdf");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Некорректный идентификатор задачи",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(400,
                response.statusCode(),
                "Код ответа не совпадает");
    }

    @Test
    void testCorrectAddTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest
                        .BodyPublishers
                        .ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Задача успешно создана",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(201,
                response.statusCode(),
                "Код ответа не совпадает");
        assertEquals(manager.getTask(1), task1,
                "Задача добавляется не корректно");

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Невозможно добавить задачу. Задача с таким ID уже есть. Используйте PUT для обновления",
                response.body(),
                "Тело ответа не совпадает при добавлении задачи, которая существует");
        assertEquals(409,
                response.statusCode(),
                "Код ответа не совпадает");

        url = URI.create("http://localhost:8082/tasks/task/");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest
                        .BodyPublishers
                        .ofString("sdfg345"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Некорректное содержимое тела запроса",
                response.body(),
                "Тело ответа не совпадает при попытке добавить кривую задачу");
        assertEquals(400,
                response.statusCode(),
                "Код ответа не совпадает");
    }

    @Test
    void testCorrectAddEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/epic/");
        Epic epic3 = new Epic("name.epic1", "description.epic1", 1, Status.NEW);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest
                        .BodyPublishers
                        .ofString(gson.toJson(epic3)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Эпик успешно создан",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(201,
                response.statusCode(),
                "Код ответа не совпадает");
        assertEquals(manager.getTask(1), epic3,
                "Эпик добавляется не корректно");

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Невозможно добавить эпик. Эпик с таким ID уже есть. Используйте PUT для обновления",
                response.body(),
                "Тело ответа не совпадает при добавлении задачи, которая существует");
        assertEquals(409,
                response.statusCode(),
                "Код ответа не совпадает");

        url = URI.create("http://localhost:8082/tasks/epic/");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest
                        .BodyPublishers
                        .ofString("sdfg345"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Некорректное содержимое тела запроса",
                response.body(),
                "Тело ответа не совпадает при попытке добавить кривой эпик");
        assertEquals(400,
                response.statusCode(),
                "Код ответа не совпадает");
    }

    @Test
    void testCorrectAddSubtask() throws IOException, InterruptedException {
        manager.addEpic(new Epic("name.epic1", "description.epic1", 1, Status.NEW));
        URI url = URI.create("http://localhost:8082/tasks/subtask/");
        Subtask subtask = new Subtask(
                "name.subtask1",
                "description.subtask1",
                2,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES),
                Instant.ofEpochSecond(1679136400),
                1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest
                        .BodyPublishers
                        .ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Подзачада успешно создана",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(201,
                response.statusCode(),
                "Код ответа не совпадает");
        assertEquals(manager.getTask(2), subtask,
                "Подзачада добавляется не корректно");

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Невозможно добавить подзадачу. Подзадача с таким ID уже есть. Используйте PUT для обновления",
                response.body(),
                "Тело ответа не совпадает при добавлении подзадачи, которая существует");
        assertEquals(409,
                response.statusCode(),
                "Код ответа не совпадает");

        url = URI.create("http://localhost:8082/tasks/subtask/");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest
                        .BodyPublishers
                        .ofString("sdfg345"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Некорректное содержимое тела запроса",
                response.body(),
                "Тело ответа не совпадает при попытке добавить кривой подзадачу");
        assertEquals(400,
                response.statusCode(),
                "Код ответа не совпадает");
    }

    @Test
    void testCorrectUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(
                "name.task",
                "description.task",
                1,
                Status.DONE,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES),
                Instant.MIN);
        URI url = URI.create("http://localhost:8082/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest
                        .BodyPublishers
                        .ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Невозможно обновить задачу",
                response.body(),
                "Тело ответа не совпадает при попытки обновления задачи, которая не существует");
        assertEquals(409,
                response.statusCode(),
                "Код ответа не совпадает");

        manager.addTask(task1);

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Задача успешно обновлена",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(201,
                response.statusCode(),
                "Код ответа не совпадает");
        assertEquals(manager.getTask(1), task,
                "Задача обновляется не корректно");

        url = URI.create("http://localhost:8082/tasks/task/?id=1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest
                        .BodyPublishers
                        .ofString("sdfg345"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Некорректное содержимое тела запроса",
                response.body(),
                "Тело ответа не совпадает при попытке добавить кривую задачу");
        assertEquals(400,
                response.statusCode(),
                "Код ответа не совпадает");
    }

    @Test
    void testCorrectUpdateEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8082/tasks/epic/?id=1");
        Epic epic3 = new Epic("name.epic1", "description.epic1", 1, Status.NEW);
        Epic epic = new Epic("name.epic", "description.epic", 1, Status.NEW);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest
                        .BodyPublishers
                        .ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Невозможно обновить эпик",
                response.body(),
                "Тело ответа не совпадает при обновлении задачи, которая не существует");
        assertEquals(409,
                response.statusCode(),
                "Код ответа не совпадает");

        manager.addEpic(epic3);

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Эпик успешно обновлен",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(201,
                response.statusCode(),
                "Код ответа не совпадает");
        assertEquals(manager.getTask(1), epic,
                "Эпик добавляется не корректно");

        url = URI.create("http://localhost:8082/tasks/epic/?id=1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest
                        .BodyPublishers
                        .ofString("sdfg345"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Некорректное содержимое тела запроса",
                response.body(),
                "Тело ответа не совпадает при попытке обновить кривой эпик");
        assertEquals(400,
                response.statusCode(),
                "Код ответа не совпадает");
    }

    @Test
    void testCorrectUpdateSubtask() throws IOException, InterruptedException {
        manager.addEpic(new Epic("name.epic1", "description.epic1", 1, Status.NEW));
        URI url = URI.create("http://localhost:8082/tasks/subtask/?id=2");
        Subtask subtask = new Subtask(
                "name.subtask1",
                "description.subtask1",
                2,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES),
                Instant.ofEpochSecond(1679136400),
                1);
        Subtask subtask3 = new Subtask(
                "1",
                "1",
                2,
                Status.DONE,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES),
                Instant.ofEpochSecond(1679136400),
                1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest
                        .BodyPublishers
                        .ofString(gson.toJson(subtask3)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Невозможно обновить подзадачу",
                response.body(),
                "Тело ответа не совпадает при добавлении подзадачи, которая не существует");
        assertEquals(409,
                response.statusCode(),
                "Код ответа не совпадает");

        manager.addSubtask(subtask);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Подзачада успешно обновлена",
                response.body(),
                "Тело ответа не совпадает");
        assertEquals(201,
                response.statusCode(),
                "Код ответа не совпадает");
        assertEquals(manager.getTask(2), subtask,
                "Подзачада добавляется не корректно");

        url = URI.create("http://localhost:8082/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest
                        .BodyPublishers
                        .ofString("sdfg345"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Некорректное содержимое тела запроса",
                response.body(),
                "Тело ответа не совпадает при попытке добавить кривой подзадачи");
        assertEquals(400,
                response.statusCode(),
                "Код ответа не совпадает");
    }
}