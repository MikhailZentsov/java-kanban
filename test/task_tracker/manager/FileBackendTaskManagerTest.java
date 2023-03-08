package task_tracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_tracker.manager.exeption.ManagerLoadException;
import task_tracker.manager.exeption.ManagerSaveException;
import task_tracker.model.Epic;
import task_tracker.model.Status;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackendTaskManagerTest extends InMemoryTaskManagerTest {
    private final Path path = Path.of("resources/test/check-methods.csv");
    private static FileBackendTaskManager manager;

    @BeforeEach
    public void beforeEach(){
        fillFile();
        manager = FileBackendTaskManager.load(path);
    }

    @Test
    void testLoad() {
        ManagerLoadException ex = assertThrows(
                ManagerLoadException.class,
                () -> FileBackendTaskManager.load(Path.of("not-exist/no-file.csv")));

        assertEquals(ex.getMessage(), "Файла загрузки не существует",
                "Отсутствие ошибки при отсутствующем файле");

        ex = assertThrows(
                ManagerLoadException.class,
                () -> FileBackendTaskManager.load(Path.of("resources/test/broken.csv")));

        assertEquals(ex.getMessage(), "Файл не удалось считать",
                "Отсутствие ошибки при битом файле");

        ex = assertThrows(
                ManagerLoadException.class,
                () -> FileBackendTaskManager.load(Path.of("resources/test/empty.csv")));

        assertEquals(ex.getMessage(), "Файл пуст",
                "Отсутствие ошибки при пустом файле");

        String[] checkData = {"1,TASK,Обычная задача,Простая задача,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "2,EPIC,Важный эпик,Очень важный,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "3,SUBTASK,Подзадача1,Просто подзадача1,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "4,SUBTASK,Подзадача2,Просто подзадача2,NEW,PT0S,-1000000000-01-01T00:00:00Z,2"};

        Integer[] history = {1, 4, 2};

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    @Test
    void testDeleteAllTasks() {
        manager.deleteAllTasks();

        int counter = 0;

        try (var bw = new BufferedReader(
                new FileReader(path.toAbsolutePath().toString(), StandardCharsets.UTF_8))) {
            while (bw.readLine() != null) {
                counter++;
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка чтения файла");
        }

        assertEquals(counter, 1,
                "В файле остались лишние строки");
    }

    @Test
    void testGetAnyTask() {
        manager.getAnyTask(1);

        manager = FileBackendTaskManager.load(path);

        String[] checkData = {"1,TASK,Обычная задача,Простая задача,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "2,EPIC,Важный эпик,Очень важный,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "3,SUBTASK,Подзадача1,Просто подзадача1,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "4,SUBTASK,Подзадача2,Просто подзадача2,NEW,PT0S,-1000000000-01-01T00:00:00Z,2"};

        Integer[] history = {4, 2, 1};

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    @Test
    void testAddTask() {
        Task task = new Task("Просто задача", "Описание просто задачи");

        manager.addTask(task);

        manager = FileBackendTaskManager.load(path);

        String[] checkData = {"1,TASK,Обычная задача,Простая задача,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "5,TASK,Просто задача,Описание просто задачи,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "2,EPIC,Важный эпик,Очень важный,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "3,SUBTASK,Подзадача1,Просто подзадача1,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "4,SUBTASK,Подзадача2,Просто подзадача2,NEW,PT0S,-1000000000-01-01T00:00:00Z,2"};

        Integer[] history = {1, 4, 2};

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    @Test
    void testAddEpic() {
        Epic task = new Epic("Просто задача", "Описание просто задачи");

        manager.addEpic(task);

        manager = FileBackendTaskManager.load(path);

        String[] checkData = {"1,TASK,Обычная задача,Простая задача,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "2,EPIC,Важный эпик,Очень важный,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "5,EPIC,Просто задача,Описание просто задачи,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "3,SUBTASK,Подзадача1,Просто подзадача1,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "4,SUBTASK,Подзадача2,Просто подзадача2,NEW,PT0S,-1000000000-01-01T00:00:00Z,2"};

        Integer[] history = {1, 4, 2};

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    @Test
    void testAddSubtask() {
        Subtask task = new Subtask("Просто задача", "Описание просто задачи", 2);

        manager.addSubtask(task);

        manager = FileBackendTaskManager.load(path);

        String[] checkData = {"1,TASK,Обычная задача,Простая задача,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "2,EPIC,Важный эпик,Очень важный,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "3,SUBTASK,Подзадача1,Просто подзадача1,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "4,SUBTASK,Подзадача2,Просто подзадача2,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "5,SUBTASK,Просто задача,Описание просто задачи,NEW,PT0S,-1000000000-01-01T00:00:00Z,2"};

        Integer[] history = {1, 4, 2};

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Просто задача", "Описание просто задачи", 1, Status.IN_PROGRESS);

        manager.updateTask(task);

        manager = FileBackendTaskManager.load(path);

        String[] checkData = {"1,TASK,Просто задача,Описание просто задачи,IN_PROGRESS,PT0S,-1000000000-01-01T00:00:00Z",
                "2,EPIC,Важный эпик,Очень важный,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "3,SUBTASK,Подзадача1,Просто подзадача1,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "4,SUBTASK,Подзадача2,Просто подзадача2,NEW,PT0S,-1000000000-01-01T00:00:00Z,2"};

        Integer[] history = {1, 4, 2};

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    @Test
    void testUpdateEpic() {
        Epic task = new Epic("Просто задача", "Описание просто задачи", 2, Status.IN_PROGRESS);

        manager.updateEpic(task);

        manager = FileBackendTaskManager.load(path);

        String[] checkData = {"1,TASK,Обычная задача,Простая задача,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "2,EPIC,Просто задача,Описание просто задачи,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "3,SUBTASK,Подзадача1,Просто подзадача1,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "4,SUBTASK,Подзадача2,Просто подзадача2,NEW,PT0S,-1000000000-01-01T00:00:00Z,2"};

        Integer[] history = {1, 4, 2};

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    @Test
    void testUpdateSubtask() {
        Subtask task = new Subtask("Просто задача", "Описание просто задачи", 4, Status.IN_PROGRESS, 2);

        manager.updateSubtask(task);

        manager = FileBackendTaskManager.load(path);

        String[] checkData = {"1,TASK,Обычная задача,Простая задача,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "2,EPIC,Важный эпик,Очень важный,IN_PROGRESS,PT0S,-1000000000-01-01T00:00:00Z",
                "3,SUBTASK,Подзадача1,Просто подзадача1,NEW,PT0S,-1000000000-01-01T00:00:00Z,2",
                "4,SUBTASK,Просто задача,Описание просто задачи,IN_PROGRESS,PT0S,-1000000000-01-01T00:00:00Z,2"};

        Integer[] history = {1, 4, 2};

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");

        Subtask task1 = new Subtask("Просто задача", "Описание просто задачи", 7, Status.IN_PROGRESS, 2);

        manager.updateSubtask(task1);

        assertArrayEquals(manager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(manager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    @Test
    void testDeleteAnyTask() {
        fillFile();
        FileBackendTaskManager manager = FileBackendTaskManager.load(path);

        manager.deleteAnyTask(2);

        FileBackendTaskManager newManager = FileBackendTaskManager.load(path);

        String[] checkData = {"1,TASK,Обычная задача,Простая задача,NEW,PT0S,-1000000000-01-01T00:00:00Z"};

        Integer[] history = {1};

        assertArrayEquals(newManager.getAllTasks().stream().map(Task::toSaveString).toArray(),
                checkData,
                "Данные задач загрузки не совпадают");
        assertArrayEquals(newManager.getHistory().stream().map(Task::getId).toArray(), history,
                "Данные загрузки истории не совпадают");
    }

    private void fillFile() {
        Task task = new Task("Обычная задача", "Простая задача", 1, Status.NEW);
        Epic epic = new Epic("Важный эпик", "Очень важный", 2, Status.NEW);
        Subtask subtask1 = new Subtask("Подзадача1", "Просто подзадача1", 3, Status.NEW, 2);
        Subtask subtask2 = new Subtask("Подзадача2", "Просто подзадача2", 4, Status.NEW, 2);
        String history = "1,4,2";

        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось создать файл");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(path.toAbsolutePath().toString(), StandardCharsets.UTF_8))) {

            bw.write("id,type,name,description,status,parent_epic_id");


            bw.newLine();

            bw.write(task.toSaveString());
            bw.newLine();
            bw.write(epic.toSaveString());
            bw.newLine();
            bw.write(subtask1.toSaveString());
            bw.newLine();
            bw.write(subtask2.toSaveString());
            bw.newLine();

            bw.newLine();

            bw.write(history);

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать файл");
        }
    }
}