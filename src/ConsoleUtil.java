import task_tracker.manager.TaskManager;
import task_tracker.model.Epic;
import task_tracker.model.Status;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ConsoleUtil {
    private static final String path = "resources/DataLoad.csv";
    public static int chooseMenu() {
        Scanner scanner = new Scanner(System.in);

        int userInput;

        System.out.println();
        System.out.println("Выберите действие:");
        System.out.println("1 - Загрузить задачи");
        System.out.println("2 - Показать все задачи");
        System.out.println("3 - Изменить статусы задач");
        System.out.println("4 - Удалить задачи");
        System.out.println("5 - Удалить эпики");
        System.out.println("6 - Удалить подзадачи");

        System.out.println("0 - Выход из приложения");

        try {
            userInput = scanner.nextInt();
        } catch (Throwable e) {
            userInput = -1;
            System.out.println("Необходимо число.");
        }

        return userInput;
    }

    public static List<String> readFileContents() {
        try {
            return Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл с данными. Возможно файл не находится в нужной директории.");
            return Collections.emptyList();
        }
    }

    public static void loadTasks(TaskManager taskManager) {
        List<String> fileContents = ConsoleUtil.readFileContents();

        if (fileContents.isEmpty()) System.out.println("Пустой файл");
        else {
            for (String fileContent : fileContents) {
                String[] recordContents = fileContent.split(",");

                switch (recordContents[0]) {
                    case "Epic":
                        int idEpic = Integer.parseInt(recordContents[3]);

                        if (taskManager.isContainsId(idEpic))
                            System.out.println("Задача с ID " + idEpic + "уже существует");
                        else {
                            taskManager.addEpic(idEpic, new Epic(recordContents[1]
                                    , recordContents[2]
                                    , idEpic
                                    , Status.getStatusByName(recordContents[4])));
                        }

                        break;

                    case "Subtask":
                        int idSubtask = Integer.parseInt(recordContents[3]);
                        int idParentEpic = Integer.parseInt(recordContents[5]);

                        if (taskManager.isContainsId(idSubtask))
                            System.out.println("Задача с ID " + idSubtask + "уже существует");
                        else {
                            taskManager.addSubtask(idSubtask, idParentEpic, new Subtask(recordContents[1]
                                    , recordContents[2]
                                    , idSubtask
                                    , Status.getStatusByName(recordContents[4])
                                    , idParentEpic));
                        }

                        break;

                    case "Task":
                        int idTask = Integer.parseInt(recordContents[3]);

                        if (taskManager.isContainsId(idTask))
                            System.out.println("Задача с ID " + idTask + "уже существует");
                        else {
                            taskManager.addTask(idTask, new Task(recordContents[1]
                                    , recordContents[2]
                                    , idTask
                                    , Status.getStatusByName(recordContents[4])));
                        }

                        break;

                    default:
                        System.out.println("Ошибка чтения строки: " + fileContent);
                }
            }
        }
    }

    public static void changeStatus(TaskManager taskManager) {
        taskManager.showAllTasks();
        taskManager.getSubtaskById(2).setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(taskManager.getSubtaskById(2));
        taskManager.showAllTasks();
        taskManager.getSubtaskById(2).setStatus(Status.DONE);
        taskManager.getSubtaskById(3).setStatus(Status.DONE);
        taskManager.getSubtaskById(4).setStatus(Status.DONE);
        taskManager.updateSubtask(taskManager.getSubtaskById(2));
        taskManager.updateSubtask(taskManager.getSubtaskById(3));
        taskManager.updateSubtask(taskManager.getSubtaskById(4));
        taskManager.showAllTasks();
    }
}
