package task_tracker.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void testGetSetName() {
        Task task = new Task("name_task", "description_task");

        assertEquals(task.getName(), "name_task", "Название не совпадает");

        task.setName("new_name_task");

        assertEquals(task.getName(), "new_name_task", "Новое название не совпадает");
    }

    @Test
    void testGetSetDescription() {
        Task task = new Task("name_task", "description_task");

        assertEquals(task.getDescription(), "description_task", "Описание не совпадает");

        task.setDescription("new_description_task");

        assertEquals(task.getDescription(), "new_description_task", "Описание не совпадает");
    }

    @Test
    void testGetSetId() {
        Task task = new Task("name_task", "description_task", 1, Status.NEW);

        assertEquals(task.getId(), 1, "ID не совпадает");

        task.setId(2);

        assertEquals(task.getId(), 2, "ID не совпадает");
    }

    @Test
    void testGetSetStatus() {
        Task task = new Task("name_task", "description_task", 1, Status.NEW);

        assertEquals(task.getStatus(), Status.NEW, "Статус не совпадает");

        task.setStatus(Status.DONE);

        assertEquals(task.getStatus(), Status.DONE, "Статус не совпадает");
    }

    @Test
    void testEquals() {
        Task task1 = new Task("name_task", "description_task", 2, Status.NEW);
        Task task2 = new Task("name_task", "description_task", 2, Status.NEW);

        assertEquals(task1, task2, "Идентичные задачи не совпадают");

        task1.setId(1);

        assertNotEquals(task1, task2, "Различные задачи совпадают");

        task1.setId(2);
        task1.setStatus(Status.DONE);

        assertNotEquals(task1, task2, "Различные задачи совпадают");

        task1.setStatus(Status.NEW);
        task1.setName("");

        assertNotEquals(task1, task2, "Различные задачи совпадают");

        task1.setName("name_task");
        task1.setDescription("");

        assertNotEquals(task1, task2, "Различные задачи совпадают");
    }

    @Test
    void testHashCode() {
        Task task1 = new Task("name_task1", "description_task1", 1, Status.NEW);
        Task task2 = new Task("name_task1", "description_task1", 1, Status.NEW);


        assertEquals(task1.hashCode(), task1.hashCode());
        assertEquals(task1.hashCode(), task2.hashCode());

        task2.setName("");

        assertNotEquals(task1.hashCode(), task2.hashCode());
        assertEquals(task2.hashCode(), task2.hashCode());

        task2.setId(2);

        assertNotEquals(task1.hashCode(), task2.hashCode());
        assertEquals(task2.hashCode(), task2.hashCode());
    }

    @Test
    void testToString() {
        Task task = new Task("name_task", "description_task", 1, Status.NEW);

        assertEquals(
                task.toString(),
                task.getClass().getName() +
                        "{ID='1', name='name_task', description='description_task', status='NEW', " +
                        "duration='" + Duration.ZERO + "', " +
                        "startTime='" + Instant.MIN + "'}",
                "toString() не совпадает");
    }

    @Test
    void toSaveString() {
        Task task = new Task("name_task", "description_task", 1, Status.NEW);

        assertEquals(
                task.toSaveString(),
                "1,TASK,name_task,description_task,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "toSaveString() не совпадает");
    }
}