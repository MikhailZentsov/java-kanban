package task_tracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void getParentEpic() {
        Subtask subtask = new Subtask("name_subtask", "description_subtask", 1);

        assertEquals(subtask.getParentEpicId(), 1, "Полученные эпики не совпадают");
    }

    @Test
    void testEquals() {
        Subtask subtask1 = new Subtask("name_subtask", "description_subtask", 2, Status.NEW, 1);
        Subtask subtask2 = new Subtask("name_subtask", "description_subtask", 2, Status.NEW, 1);

        assertEquals(subtask1, subtask2, "Идентичные подзадачи не совпадают");

        subtask2.setId(1);

        assertNotEquals(subtask1, subtask2, "Различные подзадачи совпадают");

        subtask2.setId(2);
        subtask2.setStatus(Status.DONE);

        assertNotEquals(subtask1, subtask2, "Различные подзадачи совпадают");

        subtask2.setStatus(Status.NEW);
        subtask2.setName("");

        assertNotEquals(subtask1, subtask2, "Различные подзадачи совпадают");

        subtask2.setName("name_subtask");
        subtask2.setDescription("");

        assertNotEquals(subtask1, subtask2, "Различные подзадачи совпадают");
    }

    @Test
    void testHashCode() {
        Subtask subtask1 = new Subtask("name_subtask1", "description_subtask1", 1, Status.NEW, 1);
        Subtask subtask2 = new Subtask("name_subtask1", "description_subtask1", 1, Status.NEW, 1);

        assertEquals(subtask1.hashCode(), subtask1.hashCode());
        assertEquals(subtask1.hashCode(), subtask2.hashCode());

        subtask2.setId(2);

        assertNotEquals(subtask1.hashCode(), subtask2.hashCode());
        assertEquals(subtask2.hashCode(), subtask2.hashCode());
    }

    @Test
    void testToString() {
        Subtask subtask1 = new Subtask("name_subtask1", "description_subtask1", 2, Status.NEW, 1);

        assertEquals(
                subtask1.toString(),
                subtask1.getClass().getName() +
                        "{ID='2', name='name_subtask1', description='description_subtask1', status='NEW', parentEpicID='1'}",
                "toString() не совпадает");
    }

    @Test
    void toSaveString() {
        Subtask subtask1 = new Subtask("name_subtask1", "description_subtask1", 2, Status.NEW, 1);

        assertEquals(
                subtask1.toSaveString(),
                "2,SUBTASK,name_subtask1,description_subtask1,NEW,1",
                "toSaveString() не совпадает");
    }
}