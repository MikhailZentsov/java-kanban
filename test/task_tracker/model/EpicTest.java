package task_tracker.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testGetAddSubtask() {
        Epic epic = new Epic("name_epic", "description_epic");
        Subtask subtask1 = new Subtask(
                "name_subtask1",
                "description_subtask1",
                2,
                Status.NEW,
                epic.getId());
        Subtask subtask2 = new Subtask(
                "name_subtask2",
                "description_subtask2",
                3,
                Status.NEW,
                epic.getId());

        assertArrayEquals(epic.getSubtasks().toArray(), new Integer[0], "Пустой список подзадач отличается");

        epic.addSubtask(subtask1.getId());
        epic.addSubtask(subtask2.getId());

        List<Integer> subtasks = new ArrayList<>();

        subtasks.add(subtask1.getId());
        subtasks.add(subtask2.getId());

        assertArrayEquals(epic.getSubtasks().toArray(), subtasks.toArray(), "Список подзадач отличается");
        assertFalse(epic.addSubtask(2), "Ошибка при добавлении существующей подзадачи");
    }

    @Test
    void testRemoveSubtask() {
        Epic epic = new Epic("name_epic", "description_epic");
        Subtask subtask1 = new Subtask(
                "name_subtask1",
                "description_subtask1",
                2,
                Status.NEW,
                epic.getId());
        Subtask subtask2 = new Subtask(
                "name_subtask2",
                "description_subtask2",
                3,
                Status.NEW,
                epic.getId());

        epic.addSubtask(subtask1.getId());
        epic.addSubtask(subtask2.getId());

        epic.removeSubtask(subtask2.getId());

        List<Integer> subtasks = new ArrayList<>();
        subtasks.add(subtask1.getId());

        assertArrayEquals(epic.getSubtasks().toArray(), subtasks.toArray(), "Список подзадач отличается");
        assertFalse(epic.removeSubtask(1), "Ошибка при удалении не существующей подзадачи");
    }

    @Test
    void testRemoveAllSubtasks() {
        Epic epic = new Epic("name_epic", "description_epic", 1, Status.NEW);
        Subtask subtask1 = new Subtask(
                "name_subtask1",
                "description_subtask1",
                2,
                Status.NEW,
                epic.getId());
        Subtask subtask2 = new Subtask(
                "name_subtask2",
                "description_subtask2",
                3,
                Status.NEW,
                epic.getId());

        epic.addSubtask(subtask1.getId());
        epic.addSubtask(subtask2.getId());

        epic.removeAllSubtasks();

        assertArrayEquals(epic.getSubtasks().toArray(), new Integer[0], "Список подзадач отличается");
    }

    @Test
    void testEquals() {
        Epic epic1 = new Epic("name_epic", "description_epic", 1, Status.NEW);
        Epic epic2 = new Epic("name_epic", "description_epic", 1, Status.NEW);

        assertEquals(epic1, epic2, "Идентичные эпики не совпадают.");

        epic2.setId(2);

        assertNotEquals(epic1, epic2, "Различные эпики совпадают.");

        epic2.setId(1);
        epic2.setStatus(Status.DONE);

        assertNotEquals(epic1, epic2, "Различные эпики совпадают.");

        epic2.setStatus(Status.NEW);
        epic2.setName("");

        assertNotEquals(epic1, epic2, "Различные эпики совпадают.");

        epic2.setName("name_epic");
        epic2.setDescription("");

        assertNotEquals(epic1, epic2, "Различные эпики совпадают.");

        epic2.setDescription("description_epic");
        epic2.addSubtask(3);

        assertNotEquals(epic1, epic2, "Различные эпики совпадают.");
    }

    @Test
    void testHashCode() {
        Epic epic1 = new Epic("name_epic", "description_epic", 1, Status.NEW);
        Epic epic2 = new Epic("name_epic", "description_epic", 1, Status.NEW);

        assertEquals(epic1.hashCode(), epic1.hashCode());
        assertEquals(epic1.hashCode(), epic2.hashCode());

        epic1.addSubtask(2);

        assertNotEquals(epic1.hashCode(), epic2.hashCode());
        assertEquals(epic2.hashCode(), epic2.hashCode());
    }

    @Test
    void testToString() {
        Epic epic = new Epic("name_epic", "description_epic", 1, Status.NEW);

        assertEquals(
                epic.toString(),
                epic.getClass().getName() +
                        "{ID='1', name='name_epic', description='description_epic', status='NEW', " +
                        "duration='" + Duration.ZERO + "', " +
                        "startTime='" + Instant.MIN + "', subtasksID=[Empty]}",
                "toString() не совпадает с необходимым");

        Subtask subtask = new Subtask(
                "name_subtask1",
                "description_subtask1",
                2,
                Status.DONE,
                epic.getId());

        epic.addSubtask(subtask.getId());

        assertEquals(
                epic.toString(),
                epic.getClass().getName() +
                        "{ID='1', name='name_epic', description='description_epic', status='NEW', " +
                        "duration='" + Duration.ZERO + "', " +
                        "startTime='" + Instant.MIN + "', subtasksID=[2]}",
                "toString() не совпадает");
    }

    @Test
    void testToSaveString() {
        Epic epic = new Epic("name_epic", "description_epic", 1, Status.NEW);

        assertEquals(
                epic.toSaveString(),
                "1,EPIC,name_epic,description_epic,NEW,PT0S,-1000000000-01-01T00:00:00Z",
                "toSaveString() не совпадает");
    }
}