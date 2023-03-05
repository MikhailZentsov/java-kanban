package task_tracker.manager;

import org.junit.jupiter.api.Test;
import task_tracker.model.Status;
import task_tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void testAddGet() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        assertArrayEquals(manager.getHistory().toArray(new Task[0]), new Task[0],
                "История должна быть пуста");

        Task task = new Task("name", "description", 1, Status.IN_PROGRESS);
        manager.add(task);

        assertArrayEquals(manager.getHistory().toArray(new Task[0]),
                List.of(new Task("name", "description", 1, Status.IN_PROGRESS)).toArray(),
                "В истории должна быть одна задача");

        manager.add(task);

        assertArrayEquals(manager.getHistory().toArray(new Task[0]),
                List.of(new Task("name", "description", 1, Status.IN_PROGRESS)).toArray(),
                "В истории должна быть одна задача");

        Task task2 = new Task("name", "description", 2, Status.NEW);
        manager.add(task2);

        assertArrayEquals(manager.getHistory().toArray(new Task[0]),
                List.of(new Task("name", "description", 1, Status.IN_PROGRESS),
                        new Task("name", "description", 2, Status.NEW)).toArray(),
                "В истории должно быть две задачи");

        manager.add(task);

        assertArrayEquals(manager.getHistory().toArray(new Task[0]),
                List.of(new Task("name", "description", 2, Status.NEW),
                        new Task("name", "description", 1, Status.IN_PROGRESS)).toArray(),
                "Нарушен порядок истории");
    }

    @Test
    void testRemove() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();
        Task task1 = new Task("name", "description", 1, Status.IN_PROGRESS);
        Task task2 = new Task("name", "description", 2, Status.NEW);
        Task task3 = new Task("name", "description", 3, Status.DONE);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.remove(2);

        assertArrayEquals(manager.getHistory().toArray(new Task[0]),
                List.of(
                        new Task("name", "description", 1, Status.IN_PROGRESS),
                        new Task("name", "description", 3, Status.DONE)
                ).toArray(),
                "В истории при удалении не с концов происходит ошибка");

        manager.add(task2);
        manager.add(task3);

        manager.remove(3);

        assertArrayEquals(manager.getHistory().toArray(new Task[0]),
                List.of(
                        new Task("name", "description", 1, Status.IN_PROGRESS),
                        new Task("name", "description", 2, Status.NEW)
                ).toArray(),
                "В истории при удалении с конца происходит ошибка");

        manager.add(task3);

        manager.remove(1);

        assertArrayEquals(manager.getHistory().toArray(new Task[0]),
                List.of(
                        new Task("name", "description", 2, Status.NEW),
                        new Task("name", "description", 3, Status.DONE)
                ).toArray(),
                "В истории при удалении с конца происходит ошибка");
    }

    @Test
    void testClear() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();
        Task task1 = new Task("name", "description", 1, Status.IN_PROGRESS);
        Task task2 = new Task("name", "description", 2, Status.NEW);
        Task task3 = new Task("name", "description", 3, Status.DONE);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.clear();

        assertArrayEquals(manager.getHistory().toArray(new Task[0]), new Task[0],
                "История должна быть пуста");
    }
}