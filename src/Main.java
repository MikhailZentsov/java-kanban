import task_tracker.manager.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Вас приветствует программа \"Трекер задач\"");

        while (true) {

            switch (ConsoleUtil.chooseMenu()) {
                case(0):
                    System.out.println("Программа завершила свою работу.");
                    return;

                case (1):
                    ConsoleUtil.loadTasks(taskManager);
                    break;

                case (2):
                    taskManager.showAllTasks();
                    break;

                case (3):
                    ConsoleUtil.changeStatus(taskManager);
                    break;

                default:
                    System.out.println("Такой команды не существует.");
            }
        }
    }
}
