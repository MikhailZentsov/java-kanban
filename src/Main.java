import task_tracker.manager.FileBackendTaskManager;
import task_tracker.manager.Managers;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String path = "resources/save.csv";
        Managers managers = new Managers(Paths.get(path));
        FileBackendTaskManager taskManager = (FileBackendTaskManager) managers.getManager();

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

                    break;

                case (5):

                    break;

                case (6):

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
