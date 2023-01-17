import task_tracker.manager.InMemoryTaskManager;
import task_tracker.manager.Managers;

public class Main {

    public static void main(String[] args) {
        Managers managers = new Managers();
        InMemoryTaskManager taskManager = (InMemoryTaskManager) managers.getDefault();

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
                    ConsoleUtil.showAllTasks(taskManager);
                    break;

                case (3):
                    ConsoleUtil.changeStatus(taskManager);
                    break;

                case (4):
                    taskManager.clearTasks();
                    break;

                case (5):
                    taskManager.clearEpics();
                    break;

                case (6):
                    taskManager.clearSubtasks();
                    break;

                case (7):
                    ConsoleUtil.showHistory(taskManager);
                    break;

                default:
                    System.out.println("Такой команды не существует.");
            }
        }
    }
}
