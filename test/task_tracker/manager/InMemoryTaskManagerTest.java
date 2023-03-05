package task_tracker.manager;

import org.junit.jupiter.api.Test;
import task_tracker.model.Epic;
import task_tracker.model.Status;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void testGetDeleteAllTasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

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
        Task task = new Task("name_task", "description_task", 4, Status.NEW);

        assertArrayEquals(taskManager.getAllTasks().toArray(), new Task[0],
                "Некорректный пустой список");

        taskManager.epics.put(1, epic);
        taskManager.subtasks.put(2, subtask1);
        taskManager.tasks.put(4, task);

        List<Task> list = new ArrayList<>();
        list.add(task);
        list.add(epic);
        list.add(subtask1);

        assertArrayEquals(taskManager.getAllTasks().toArray(), list.toArray(new Task[0]),
                "Некорректный список из 1 задачи, 1 эпика, 1 подзадачи");

        taskManager.subtasks.put(3, subtask2);
        list.add(subtask2);

        assertArrayEquals(taskManager.getAllTasks().toArray(), list.toArray(new Task[0]),
                "Некорректный список из 1 задачи, 1 эпика, 2 подзадач");

        taskManager.deleteAllTasks();

        assertArrayEquals(taskManager.getAllTasks().toArray(), new Task[0],
                "Некорректный список после удаления всех задач");
    }

    @Test
    void testGetAnyTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("name_epic", "description_epic", 1, Status.NEW);

        assertNull(taskManager.getAnyTask(5),
                "Получение задачи не возвращает null, если нет задачи");

        taskManager.epics.put(1, epic);

        assertEquals(taskManager.getAnyTask(1), epic,
                "Не возращает задачу, если она есть");
    }

    @Test
    void testAddTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("name_task", "description_task", 4, Status.DONE);

        assertFalse(taskManager.addTask(null),
                "При добавлении null вместо задачи не возвращает false");
        assertTrue(taskManager.addTask(task),
                "При добавлении задачи не возращает true");
        assertEquals(taskManager.getAnyTask(1),
                new Task("name_task", "description_task", 1, Status.NEW),
                "После добавления задачи не корректно назначен ID");
        assertEquals(taskManager.getAnyTask(1).getStatus(), Status.NEW,
                "После добавления задачи не корректно назначен статус");
    }

    @Test
    void testAddEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("name_epic", "description_epic", 7, Status.NEW);

        assertFalse(taskManager.addEpic(null),
                "При добавлении null вместо эпика не возвращает false");
        assertTrue(taskManager.addEpic(epic),
                "При добавлении эпика не возращает true");
        assertEquals(taskManager.getAnyTask(1),
                new Epic("name_epic", "description_epic", 1, Status.NEW),
                "После добавления эпика не корректно назначен ID");
        assertArrayEquals(((Epic) taskManager.getAnyTask(1)).getSubtasks().toArray(),
                new Subtask[0],
                "После добавления эпика не обнулён список подзадач");
        assertEquals(taskManager.getAnyTask(1).getStatus(), Status.NEW,
                "После добавления эпика не корректно назначен статус");
    }

    @Test
    void testAddSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("name_epic", "description_epic", 1, Status.NEW);
        Subtask subtask1 = new Subtask(
                "name_subtask1",
                "description_subtask1",
                2,
                Status.DONE,
                1);
        Subtask subtask2 = new Subtask(
                "name_subtask2",
                "description_subtask2",
                3,
                Status.NEW,
                5);

        taskManager.addEpic(epic);

        assertFalse(taskManager.addSubtask(null),
                "При добавлении null вместо подзадачи не возвращает false");
        assertFalse(taskManager.addSubtask(subtask2),
                "При добавлении подзадачи с не существующим эпиком не возвращает false");
        assertTrue(taskManager.addSubtask(subtask1),
                "При добавлении подзадачи не возращает true");
        assertEquals(taskManager.getAnyTask(2),
                new Subtask("name_subtask1", "description_subtask1", 2, Status.NEW, 1),
                "После добавления подзадачи не корректно назначен ID");
        assertArrayEquals(((Epic) taskManager.getAnyTask(1)).getSubtasks().toArray(),
                List.of(2).toArray(),
                "После добавления подзадачи не добавлена к родительскому эпику");
        assertEquals(taskManager.getAnyTask(2).getStatus(), Status.NEW,
                "После добавления эпика не корректно назначен статус");
    }

    @Test
    void testUpdateTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("name_task1", "description_task1", 1, Status.NEW);
        Task updateTask = new Task("name_task2", "description_task2", 1, Status.DONE);
        Task wrongTask = new Task("name_task3", "description_task3", 7, Status.NEW);

        taskManager.addTask(task);

        assertFalse(taskManager.updateTask(null),
                "При обновлении null вместо задачи не возвращает false");
        assertTrue(taskManager.updateTask(updateTask),
                "При обновлении правильной задачи не возращает true");
        assertFalse(taskManager.updateTask(wrongTask),
                "При обновлении задачи с ошибочным ID не возвращает false");
        assertEquals(taskManager.getAnyTask(1),
                new Task("name_task2", "description_task2", 1, Status.DONE),
                "После обновления задачи не корректно обновлены поля");
        assertEquals(taskManager.getAnyTask(1).getStatus(), Status.DONE,
                "После обновления задачи не корректно назначен статус");
    }

    @Test
    void testUpdateEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("name_epic1", "description_epic1", 1, Status.NEW);
        Epic updateEpic = new Epic("name_epic2", "description_epic2", 1, Status.DONE);
        Epic wrongEpic = new Epic("name_epic3", "description_epic3", 7, Status.NEW);

        taskManager.addEpic(epic);

        assertFalse(taskManager.updateEpic(null),
                "При обновлении null вместо эпика не возвращает false");
        assertTrue(taskManager.updateEpic(updateEpic),
                "При добавлении правильного эпика не возращает true");
        assertFalse(taskManager.updateEpic(wrongEpic),
                "При добавлении эпика с ошибочным ID не возвращает false");
        assertEquals(taskManager.getAnyTask(1),
                new Epic("name_epic2", "description_epic2", 1, Status.NEW),
                "После обновления эпика не корректно обновлены поля");
    }

    @Test
    void testUpdateSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("name_epic1", "description_epic1");
        Epic epic2 = new Epic("name_epic2", "description_epic2");

        Subtask subtask = new Subtask(
                "name_subtask1",
                "description_subtask1",
                1);
        Subtask updateSubtask = new Subtask(
                "name_subtask2",
                "description_subtask2",
                3,
                Status.IN_PROGRESS,
                1);
        Subtask wrongSubtask = new Subtask(
                "name_subtask3",
                "description_subtask3",
                11,
                Status.NEW,
                1);
        Subtask moveWrongSubtask = new Subtask(
                "name_subtask4",
                "description_subtask4",
                3,
                Status.IN_PROGRESS,
                55);
        Subtask moveUpdateSubtask = new Subtask(
                "name_subtask5",
                "description_subtask5",
                3,
                Status.IN_PROGRESS,
                2);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.addSubtask(subtask);

        assertFalse(taskManager.updateSubtask(null),
                "При обновлении null вместо подзадачи не возвращает false");
        assertTrue(taskManager.updateSubtask(updateSubtask),
                "При добавлении правильной подзадачи не возращает true");
        assertFalse(taskManager.updateSubtask(wrongSubtask),
                "При добавлении подзадачи с несуществующим ID не возвращает false");
        assertFalse(taskManager.updateSubtask(moveWrongSubtask),
                "При добавлении подзадачи с несуществующим ID родительского эпика не возвращает false");
        assertEquals(taskManager.getAnyTask(3),
                new Subtask("name_subtask2",
                        "description_subtask2",
                        3,
                        Status.IN_PROGRESS,
                        1),
                "После обновления подзадачи не корректно обновлены поля");
        assertTrue(taskManager.updateSubtask(moveUpdateSubtask),
                "При добавлении правильной подзадачи не возращает true");
        assertArrayEquals(epic1.getSubtasks().toArray(), new Subtask[0],
                "При перемещении подзадачи к другому эпику из старого не удалилась");
        assertArrayEquals(epic2.getSubtasks().toArray(), List.of(3).toArray(),
                "При перемещении подзадачи к другому эпику в новый не добавилось");
        assertEquals(((Subtask) taskManager.getAnyTask(3)).getParentEpicId(), 2,
                "При перемещении подзадачи не изменился родительский эпик");
    }

    @Test
    void testDeleteAnyTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("name_epic", "description_epic");

        Subtask subtask = new Subtask(
                "name_subtask",
                "description_subtask",
                1);
        Subtask subtask2 = new Subtask(
                "name_subtask",
                "description_subtask",
                1);

        Task task = new Task("name_task", "description_task", 1, Status.NEW);

        taskManager.addTask(task);

        assertFalse(taskManager.deleteAnyTask(4),
                "При попытке удаления несуществующей задачи не возвращает false");

        taskManager.deleteAnyTask(1);

        assertArrayEquals(taskManager.getAllTasks().toArray(), new Task[0],
                "При удалении простой задачи список не пуст");

        taskManager = new InMemoryTaskManager();

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        taskManager.deleteAnyTask(2);

        assertArrayEquals(taskManager.getAllTasks().toArray(), List.of(epic).toArray(),
                "При удалении подзадачи список отличается");

        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask2);

        taskManager.deleteAnyTask(1);

        assertArrayEquals(taskManager.getAllTasks().toArray(), new Task[0],
                "При удалении подзадачи не были удалены эпик и подзадачи");
    }

    @Test
    void testGetEpicSubtasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("name_epic", "description_epic");

        Subtask subtask = new Subtask(
                "name_subtask",
                "description_subtask",
                1);
        Subtask subtask2 = new Subtask(
                "name_subtask",
                "description_subtask",
                1);

        taskManager.addEpic(epic);

        assertArrayEquals(taskManager.getEpicSubtasks(1).toArray(), new Task[0],
                "Ошибка при получении списка подзадач у эпика без подзадач");

        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask2);

        List<Task> list = new ArrayList<>();
        list.add(subtask);
        list.add(subtask2);

        assertArrayEquals(taskManager.getEpicSubtasks(1).toArray(), list.toArray(),
                "Ошибка при получении списка подзадач у эпика с задачами");
    }

    @Test
    void testGetHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("name_epic", "description_epic");

        Subtask subtask = new Subtask(
                "name_subtask",
                "description_subtask",
                1);
        Subtask subtask2 = new Subtask(
                "name_subtask",
                "description_subtask",
                1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask2);

        assertArrayEquals(taskManager.getHistory().toArray(), new Task[0],
                "Ошибка при получении пустой истории");

        taskManager.getAnyTask(1);
        taskManager.getAnyTask(2);

        List<Task> list = new ArrayList<>();
        list.add(epic);
        list.add(subtask);

        assertArrayEquals(taskManager.getHistory().toArray(), list.toArray(),
                "Ошибка при получении пустой истории");
    }

    @Test
    void testEpicStatus() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("name_epic", "description_epic", 1, Status.DONE);

        taskManager.addEpic(epic);

        assertEquals(
                epic.getStatus(),
                Status.NEW,
                "При создании статус эпика отличен от NEW");

        Subtask subtask1 = new Subtask(
                "name_subtask1",
                "description_subtask1",
                1);
        Subtask subtask2 = new Subtask(
                "name_subtask2",
                "description_subtask2",
                1);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(
                epic.getStatus(),
                Status.NEW,
                "При добавлении новых подзадач статус эпика отличен от NEW");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(
                epic.getStatus(),
                Status.DONE,
                "При завершении всех подзадач статус эпика отличен от DONE");

        subtask2.setStatus(Status.NEW);

        taskManager.updateSubtask(subtask2);

        assertEquals(
                epic.getStatus(),
                Status.IN_PROGRESS,
                "Одна звершенная подзадача, одна новая. Статус эпика отличен от IN_PROGRESS");

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);

        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(
                epic.getStatus(),
                Status.IN_PROGRESS,
                "Все подзадачи в процессе выполненеия. Статус эпика отличен от IN_PROGRESS");

        subtask1.setStatus(Status.DONE);

        taskManager.updateSubtask(subtask1);
        taskManager.deleteAnyTask(3);

        assertEquals(
                epic.getStatus(),
                Status.DONE,
                "Подзадачу удалили, осталась одна подзадача DONE. Статус эпика отличен от DONE");

        taskManager.deleteAnyTask(2);

        assertEquals(
                epic.getStatus(),
                Status.NEW,
                "После удаления всех подзадач статус эпика отличен от NEW");
    }
}