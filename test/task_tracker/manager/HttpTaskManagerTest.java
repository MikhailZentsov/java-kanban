package task_tracker.manager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task_tracker.model.*;
import task_tracker.server.HttpTaskServer;
import task_tracker.server.KVServer;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    private static HttpTaskManager manager;
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private static Task task1;
    private static Task task2;
    private static Epic epic1;
    private static Epic epic2;
    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer("localhost", 8085);
        kvServer.start();
        manager = (HttpTaskManager) Managers.getDefault(new URL("http://localhost:8085"));
        taskServer = new HttpTaskServer(manager, "localhost", 8081);
        taskServer.start();
        task1 = new Task(
                "name.task1",
                "description.task1",
                1,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES * 2),
                Instant.ofEpochSecond(1678136400));
        task2 = new Task(
                "name.task2",
                "description.task2",
                2,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES * 2),
                Instant.ofEpochSecond(1678309200));
        epic1 = new Epic("name.epic1", "description.epic1", 3, Status.NEW);
        epic2 = new Epic("name.epic2", "description.epic2", 4, Status.NEW);
        subtask1 = new Subtask(
                "name.subtask1",
                "description.subtask1",
                5,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES),
                Instant.ofEpochSecond(1679136400),
                3);
        subtask2 = new Subtask(
                "name.subtask1",
                "description.subtask1",
                6,
                Status.NEW,
                Duration.ofMinutes(InMemoryTaskManager.PLANNING_PERIOD_MINUTES),
                Instant.ofEpochSecond(1689136400),
                3);
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

    @Test
    void testCorrectSaveAndLoad() throws IOException {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.getTask(1);
        manager.getTask(3);
        List<Task> historyList = new ArrayList<>(manager.getHistory());

        manager = (HttpTaskManager) Managers.getDefault(new URL("http://localhost:8085"));

        List<Task> taskList = new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);
        taskList.add(epic1);
        taskList.add(epic2);
        taskList.add(subtask1);
        taskList.add(subtask2);

        assertArrayEquals(manager.getTasks().toArray(), taskList.toArray(),
                "Задачи из загрузки не совпадают");
        assertArrayEquals(manager.getHistory().toArray(), historyList.toArray(),
                "История из загрузки не совпадают");
    }
}