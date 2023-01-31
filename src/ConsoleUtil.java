import org.jetbrains.annotations.NotNull;
import task_tracker.manager.InMemoryTaskManager;
import task_tracker.model.Epic;
import task_tracker.model.Status;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.util.List;
import java.util.Scanner;

public class ConsoleUtil {
    private static final Scanner scanner = new Scanner(System.in);
    public static int chooseMenu() {

        int userInput;

        System.out.println();
        System.out.println("Выберите действие:");
        System.out.println("1 - Загрузить задачи");
        System.out.println("2 - Показать все задачи");
        System.out.println("3 - Изменить статусы задач");
        System.out.println("4 - Удалить задачи");
        System.out.println("5 - Удалить эпики");
        System.out.println("6 - Удалить подзадачи");
        System.out.println("7 - Показать историю");
        System.out.println("8 - Показать задачу");
        System.out.println("9 - Удалить задачу");

        System.out.println("0 - Выход из приложения");

        try {
            userInput = scanner.nextInt();
        } catch (Throwable e) {
            userInput = -1;
            System.out.println("Необходимо число.");
        }

        return userInput;
    }

    public static void loadTasks(@NotNull InMemoryTaskManager taskManager) {
        Epic epic1 = new Epic("Переезд", "Переезд в другую квартиру");
        Epic epic2 = new Epic("Важный эпик 2", "Очень важный");
        Epic epic3 = new Epic("Важный эпик 2", "Очень важный");
        Epic epic4 = new Epic("Важный эпик 2", "Очень важный");

        taskManager.addTask(new Task("Обычная задача","Простая задача"));
        taskManager.addTask(new Task("Поесть","Не забыть поесть!"));
        taskManager.addTask(new Task("Почитать перед сном","Почитать перед сном про Java"));

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        taskManager.addEpic(epic4);

        taskManager.addSubtask(new Subtask("Собрать коробки", "Собрать вещи в коробки", epic1));
        taskManager.addSubtask(new Subtask("Упаковать кошку", "Упаковать кошку в переноску", epic1));
        taskManager.addSubtask(new Subtask("Сказать слова прощания", "Добрые слова прощания", epic1));

        taskManager.addSubtask(new Subtask("Задача 1", "", epic2));
        taskManager.addSubtask(new Subtask("Задача 1", "", epic2));

        taskManager.addSubtask(new Subtask("Задача 1", "", epic3));
        taskManager.addSubtask(new Subtask("Задача 1", "", epic3));

        System.out.println("Задачи загружены в систему.");
    }

    public static void changeStatus(@NotNull InMemoryTaskManager taskManager) {
        List<Subtask> subtasks = taskManager.getSubtasks();
        for (Subtask subtask : subtasks) {
            subtask.setStatus(Status.DONE);
            taskManager.addSubtask(subtask);
            taskManager.deleteSubtask(subtask.getId());
        }
    }

    public static void showAllTasks(@NotNull InMemoryTaskManager taskManager) {
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.toString());
        }

        for (Subtask task : taskManager.getSubtasks()) {
            System.out.println(task.toString());
        }

        for (Task task : taskManager.getTasks()) {
            System.out.println(task.toString());
        }
    }

    public static void showHistory(@NotNull InMemoryTaskManager taskManager) {
        for (Task task: taskManager.getHistory()) {
            System.out.println(task.toString());
        }
    }

    public static void showTask(InMemoryTaskManager taskManager) {
        int taskId = -1;

        System.out.print("Введите ID задачи: ");

        try {
            taskId = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Вы ввели не число.");
        }

        Task task = taskManager.getAnyTaskById(taskId);

        if (task != null) {
            System.out.println(task);
        } else {
            System.out.println("Задачи с таким ID нет.");
        }
    }

    public static void deleteTasks(InMemoryTaskManager taskManager) {
        int taskId = -1;

        System.out.print("Введите ID задачи: ");

        try {
            taskId = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Вы ввели не число.");
        }

        if (taskManager.deleteTask(taskId) ||
                taskManager.deleteSubtask(taskId) ||
                taskManager.deleteEpic(taskId)) {
            System.out.println("Задача удалена.");
        } else {
            System.out.println("Такой задачи нет.");
        }
    }
}
