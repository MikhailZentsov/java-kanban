import task_tracker.manager.Managers;
import task_tracker.manager.TaskManager;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String path = "resources/save.csv";
        TaskManager taskManager = Managers.getManager();

        System.out.println("Вас приветствует программа \"Трекер задач\"");

        while (true) {

            switch (ConsoleUtil.chooseMenu()) {
                case(0):
                    System.out.println("Программа завершила свою работу.");
                    return;

                case (1):
                    ConsoleUtil.createTasks(taskManager);
                    break;

                case (2):
                    ConsoleUtil.showAllTasks(taskManager);
                    break;

                case (3):
                    System.out.println("Метод отключен");
                    break;

                case (4):
                    System.out.println("Метод отключен");
                    break;

                case (5):
                    System.out.println("Метод отключен");
                    break;

                case (6):
                    System.out.println("Метод отключен");
                    break;

                case (7):
                    ConsoleUtil.showHistory(taskManager);
                    break;

                case (8):
                    ConsoleUtil.showTask(taskManager);
                    break;

                case (9):
                    ConsoleUtil.deleteTasks(taskManager);
                    break;

                default:
                    System.out.println("Такой команды не существует.");
            }
        }
    }
}
