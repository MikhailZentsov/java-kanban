import task_tracker.manager.HttpTaskManager;
import task_tracker.manager.Managers;
import task_tracker.server.HttpTaskServer;
import task_tracker.server.KVServer;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Main {
    private static final String hostname = "localhost";

    public static void main(String[] args) throws IOException {
        System.out.println("Вас приветствует программа \"Трекер задач\"");

        KVServer kvServer = new KVServer(hostname, 8085);
        kvServer.start();

        HttpTaskManager taskManager = (HttpTaskManager) Managers.getManager(new URL("http://" + hostname + 8085));

        HttpTaskServer taskServer = new HttpTaskServer(taskManager, hostname, 8081);
        taskServer.start();

        System.out.println("Для завершения нажмите 0");
        if (new Scanner(System.in).nextInt() == 0) {
            kvServer.stop(0);
            taskServer.stop(0);
        }
    }
}
