package task_tracker;

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
}
