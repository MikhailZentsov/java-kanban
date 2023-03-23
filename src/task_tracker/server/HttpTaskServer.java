package task_tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import task_tracker.adapter.DurationAdapter;
import task_tracker.adapter.InstantAdapter;
import task_tracker.manager.HttpTaskManager;

import task_tracker.exeption.ManagerExchangeException;
import task_tracker.model.Epic;
import task_tracker.model.Subtask;
import task_tracker.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpTaskServer implements HttpHandler {
    private final int PORT;
    private final String HOSTNAME;
    private final HttpServer server;
    private final HttpTaskManager manager;
    private final Gson gson;
    public HttpTaskServer(HttpTaskManager manager, String hostname, int port) throws IOException {
        PORT = port;
        HOSTNAME = hostname;
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
        server.createContext("/tasks", this);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Доступ по http://" + HOSTNAME + ":" + PORT + "/");
        server.start();
    }

    public void stop(int delay) {
        server.stop(delay);
    }

    @Override
    public void handle(HttpExchange exchange) {
        Endpoint endpoint = getEndpoint(exchange);

        switch (endpoint) {
            case GET_PRIORITIZED_TASKS:
                handleGetPrioritizedTasks(exchange);
                break;
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK_BY_ID:
                handleGetTaskByID(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtask(exchange);
                break;
            case GET_HISTORY:
                handleGetHistory(exchange);
                break;
            case DELETE_TASKS:
                handleDeleteTasks(exchange);
                break;
            case DELETE_TASK_BY_ID:
                handleDeleteTaskById(exchange);
                break;
            case POST_TASK:
                handleAddTask(exchange);
                break;
            case POST_EPIC:
                handleAddEpic(exchange);
                break;
            case POST_SUBTASK:
                handleAddSubtask(exchange);
                break;
            case PUT_TASK:
                handleUpdateTask(exchange);
                break;
            case PUT_EPIC:
                handleUpdateEpic(exchange);
                break;
            case PUT_SUBTASK:
                handleUpdateSubtask(exchange);
                break;
            default:
                writeResponse(exchange, "Такого ендпоинта не существует", 404);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) {
        writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
    }

    private void handleGetTasks(HttpExchange exchange) {
        writeResponse(exchange, gson.toJson(manager.getTasks()), 200);
    }

    private void handleGetTaskByID(HttpExchange exchange) {
        Optional<Integer> taskIdOpt = getId(exchange);
        if (taskIdOpt.isPresent()) {
            writeResponse(exchange, gson.toJson(manager.getTask(taskIdOpt.get())), 200);
        } else {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }

    private void handleGetEpicSubtask(HttpExchange exchange) {
        Optional<Integer> taskIdOpt = getId(exchange);
        if (taskIdOpt.isPresent()) {
            writeResponse(exchange, gson.toJson(manager.getEpicSubtasks(taskIdOpt.get())), 200);
        } else {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }

    private void handleGetHistory(HttpExchange exchange) {
        writeResponse(exchange, gson.toJson(manager.getHistory()), 200);
    }

    private void handleDeleteTasks(HttpExchange exchange) {
        manager.deleteTasks();
        writeResponse(exchange, "Все задачи удалены", 200);
    }

    private void handleDeleteTaskById(HttpExchange exchange) {
        Optional<Integer> taskIdOpt = getId(exchange);
        if (taskIdOpt.isPresent()) {
            if (manager.deleteAnyTask(taskIdOpt.get())) {
                writeResponse(exchange, "Задача удалена", 201);
            } else {
                writeResponse(exchange, "Задача не найдена", 404);
            }
        } else {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }

    private void handleAddTask(HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody()) {
            Task task = gson.fromJson(new String(is.readAllBytes()), Task.class);
            if (manager.addTask(task)) {
                writeResponse(exchange, "Задача успешно создана", 201);
            } else {
                writeResponse(exchange, "Невозможно добавить задачу. " +
                        "Задача с таким ID уже есть. Используйте PUT для обновления", 409);
            }
        } catch (JsonSyntaxException | IOException e) {
            writeResponse(exchange, "Некорректное содержимое тела запроса", 400);
        }
    }

    private void handleAddEpic(HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody()) {
            Epic task = gson.fromJson(new String(is.readAllBytes()), Epic.class);
            if (manager.addEpic(task)) {
                writeResponse(exchange, "Эпик успешно создан", 201);
            } else {
                writeResponse(exchange, "Невозможно добавить эпик. " +
                        "Эпик с таким ID уже есть. Используйте PUT для обновления", 409);
            }
        } catch (JsonSyntaxException | IOException e) {
            writeResponse(exchange, "Некорректное содержимое тела запроса", 400);
        }
    }

    private void handleAddSubtask(HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody()) {
            Subtask task = gson.fromJson(new String(is.readAllBytes()), Subtask.class);
            if (manager.addSubtask(task)) {
                writeResponse(exchange, "Подзачада успешно создана", 201);
            } else {
                writeResponse(exchange, "Невозможно добавить подзадачу. " +
                        "Подзадача с таким ID уже есть. Используйте PUT для обновления", 409);
            }
        } catch (JsonSyntaxException | IOException e) {
            writeResponse(exchange, "Некорректное содержимое тела запроса", 400);
        }
    }

    private void handleUpdateTask(HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody()) {
            Task task = gson.fromJson(new String(is.readAllBytes()), Task.class);
            if (manager.updateTask(task)) {
                writeResponse(exchange, "Задача успешно обновлена", 201);
            } else {
                writeResponse(exchange, "Невозможно обновить задачу", 409);
            }
        } catch (JsonSyntaxException | IOException e) {
            writeResponse(exchange, "Некорректное содержимое тела запроса", 400);
        }
    }

    private void handleUpdateEpic(HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody()) {
            Epic task = gson.fromJson(new String(is.readAllBytes()), Epic.class);
            if (manager.updateEpic(task)) {
                writeResponse(exchange, "Эпик успешно обновлен", 201);
            } else {
                writeResponse(exchange, "Невозможно обновить эпик", 409);
            }
        } catch (JsonSyntaxException | IOException e) {
            writeResponse(exchange, "Некорректное содержимое тела запроса", 400);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody()) {
            Subtask task = gson.fromJson(new String(is.readAllBytes()), Subtask.class);
            if (manager.updateSubtask(task)) {
                writeResponse(exchange, "Подзачада успешно обновлена", 201);
            } else {
                writeResponse(exchange, "Невозможно обновить подзадачу", 409);
            }
        } catch (JsonSyntaxException | IOException e) {
            writeResponse(exchange, "Некорректное содержимое тела запроса", 400);
        }
    }

    private Endpoint getEndpoint(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Map<String, String> params = splitQuery(exchange.getRequestURI());

        if (pathParts.length >= 2) {
            if (!pathParts[1].equals("tasks")) {
                return Endpoint.UNKNOWN;
            }
        }

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED_TASKS;
            }
        }

        if (pathParts.length == 3) {
            if (params.containsKey("id")) {
                if (pathParts[2].equals("task")) {
                    switch (requestMethod){
                        case "GET":
                            return Endpoint.GET_TASK_BY_ID;
                        case "DELETE":
                            return Endpoint.DELETE_TASK_BY_ID;
                        case "PUT":
                            return Endpoint.PUT_TASK;
                    }
                }

                if (pathParts[2].equals("subtask")) {
                    if (requestMethod.equals("PUT")) {
                        return Endpoint.PUT_SUBTASK;
                    }
                }

                if (pathParts[2].equals("epic")) {
                    if (requestMethod.equals("PUT")) {
                        return Endpoint.PUT_EPIC;
                    }
                }
            }

            if (pathParts[2].equals("task")) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_TASKS;
                }

                if (requestMethod.equals("DELETE")) {
                    return Endpoint.DELETE_TASKS;
                }

                if (requestMethod.equals("POST")) {
                    return Endpoint.POST_TASK;
                }
            }

            if (pathParts[2].equals("subtask")) {
                if (requestMethod.equals("POST")) {
                    return Endpoint.POST_SUBTASK;
                }
            }

            if (pathParts[2].equals("epic")) {
                if (requestMethod.equals("POST")) {
                    return Endpoint.POST_EPIC;
                }
            }

            if (pathParts[2].equals("history")) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_HISTORY;
                }
            }
        }

        if (pathParts.length == 4) {
            if (params.containsKey("id")) {
                if (pathParts[2].equals("subtasks")) {
                    if (pathParts[3].equals("epic")) {
                        if (requestMethod.equals("GET")) {
                            return Endpoint.GET_EPIC_SUBTASKS;
                        }
                    }
                }
            }
        }

        return Endpoint.UNKNOWN;
    }

    private static Map<String, String> splitQuery(URI uri) {
        Map<String, String> query_pairs = new HashMap<>();
        String query = uri.getQuery();

        if (query != null) {
            String[] pairs = query.split("&");


            for (String pair : pairs) {
                int index = pair.indexOf("=");
                query_pairs.put(
                        URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8),
                        URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8));
            }
        }

        return query_pairs;
    }

    private Optional<Integer> getId(HttpExchange exchange) {
        Map<String, String> params = splitQuery(exchange.getRequestURI());
        try {
            return Optional.of(Integer.parseInt(params.get("id")));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) {
        try {
            if (responseString == null || responseString.isBlank()) {
                exchange.sendResponseHeaders(404, 0);
            } else {
                byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
            }
        } catch (IOException e) {
            throw new ManagerExchangeException(e.getMessage());
        } finally {
            exchange.close();
        }
    }

    enum Endpoint {
        GET_PRIORITIZED_TASKS,
        GET_TASKS,
        GET_TASK_BY_ID,
        GET_EPIC_SUBTASKS,
        GET_HISTORY,
        DELETE_TASKS,
        DELETE_TASK_BY_ID,
        POST_TASK,
        POST_EPIC,
        POST_SUBTASK,
        PUT_TASK,
        PUT_EPIC,
        PUT_SUBTASK,
        UNKNOWN
    }
}
