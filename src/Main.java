import task_tracker.TaskManager;

import static task_tracker.ConsoleUtil.chooseMenu;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Вас приветствует программа \"Трекер задач\"");

        while (true) {

            switch (chooseMenu()) {
                case(0):
                    System.out.println("Программа завершила свою работу.");
                    return;

                case (1):
                    taskManager.loadTasks();
                    break;

                case (2):
                    taskManager.showAllTasks();
                    break;

                case (3):
                    taskManager.changeStatus();
                    break;

                default:
                    System.out.println("Такой команды не существует.");
            }
        }
    }
}
