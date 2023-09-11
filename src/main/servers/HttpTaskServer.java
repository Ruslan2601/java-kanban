package main.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import main.interfaces.TaskManager;
import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.services.Managers;
import main.util.Endpoint;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;

    private TaskManager managers;

    public HttpTaskServer(TaskManager managers) throws IOException {
        this.managers = managers;
        this.gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handle);
    }

    private void handle(HttpExchange httpExchange) {
        try {
            String requestMethod = httpExchange.getRequestMethod();
            String url = httpExchange.getRequestURI().toString();
            String[] urlParts = url.split("/");

            Endpoint endpoint = getEndpoint(requestMethod, url);

            switch (endpoint) {
                case GET_TASKS:
                    handleGetTasks(httpExchange, urlParts[2]);
                    break;
                case GET_BY_ID:
                    handleGetTaskByID(httpExchange, urlParts);
                    break;
                case GET_HISTORY:
                    handleGetHistory(httpExchange);
                    break;
                case GET_PRIORITIZED:
                    handleGetPrioritized(httpExchange);
                    break;
                case DELETE_BY_ID:
                    handleDeleteTaskByID(httpExchange, urlParts);
                    break;
                case DELETE_TASKS:
                    handleDeleteTasks(httpExchange, urlParts[2]);
                    break;
                case POST_TASK:
                    handlePostTask(httpExchange, urlParts[2]);
                    break;
                case POST_UPDATE:
                    handlePostUpdateTask(httpExchange, urlParts);
                    break;
                default:
                    System.out.println("Неверный запрос - " + requestMethod + " " + url);
                    httpExchange.sendResponseHeaders(400, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void handlePostUpdateTask(HttpExchange httpExchange, String[] urlParts) throws IOException {
        Optional<Integer> optionalId = getTaskId(urlParts);
        if (optionalId.isEmpty()) {
            System.out.println("Неверный id " + urlParts[urlParts.length - 1].split("=")[1]);
            httpExchange.sendResponseHeaders(400, 0);
            return;
        }
        String body = readText(httpExchange);
        int id = optionalId.get();
        switch (urlParts[2]) {
            case "task":
                Task task = gson.fromJson(body, Task.class);
                managers.changeTask(id, task, task.getStatus());
                sendText(httpExchange, "Задача обновлена!");
                return;
            case "subtask":
                Subtask subtask = gson.fromJson(body, Subtask.class);
                managers.changeSubtask(id, subtask, subtask.getStatus());
                sendText(httpExchange, "Задача обновлена!");
                return;
            case "epic":
                EpicTask epicTask = gson.fromJson(body, EpicTask.class);
                managers.changeEpicTask(id, epicTask);
                sendText(httpExchange, "Задача обновлена!");
                return;
        }
        System.out.println("Неверный тип задачи - " + urlParts[2]);
        httpExchange.sendResponseHeaders(400, 0);
    }

    private void handlePostTask(HttpExchange httpExchange, String type) throws IOException {
        String body = readText(httpExchange);

        switch (type) {
            case "task":
                Task task = gson.fromJson(body, Task.class);
                managers.newTask(task);
                sendText(httpExchange, "Задачи добавлена!");
                return;
            case "subtask":
                Subtask subtask = gson.fromJson(body, Subtask.class);
                managers.newSubtask(subtask);
                sendText(httpExchange, "Задачи добавлена!");
                return;
            case "epic":
                EpicTask epicTask = gson.fromJson(body, EpicTask.class);
                managers.newEpicTask(epicTask);
                sendText(httpExchange, "Задачи добавлена!");
                return;
        }
        System.out.println("Неверный тип задачи - " + type);
        httpExchange.sendResponseHeaders(400, 0);
    }

    private void handleDeleteTasks(HttpExchange httpExchange, String type) throws IOException {
        switch (type) {
            case "task":
                managers.removeAllTasks();
                sendText(httpExchange, "Задачи удалены!");
                return;
            case "subtask":
                managers.removeAllSubtask();
                sendText(httpExchange, "Задачи удалены!");
                return;
            case "epic":
                managers.removeAllEpicTask();
                sendText(httpExchange, "Задачи удалены!");
                return;
        }
        System.out.println("Неверный тип задачи - " + type);
        httpExchange.sendResponseHeaders(400, 0);
    }

    private void handleDeleteTaskByID(HttpExchange httpExchange, String[] urlParts) throws IOException {
        Optional<Integer> optionalId = getTaskId(urlParts);
        if (optionalId.isEmpty()) {
            System.out.println("Неверный id " + urlParts[urlParts.length - 1].split("=")[1]);
            httpExchange.sendResponseHeaders(400, 0);
            return;
        }
        int id = optionalId.get();
        switch (urlParts[2]) {
            case "task":
                managers.removeTaskById(id);
                sendText(httpExchange, "Задача удалена!");
                return;
            case "subtask":
                managers.removeSubtaskById(id);
                sendText(httpExchange, "Задача удалена!");
                return;
            case "epic":
                managers.removeEpicTaskById(id);
                sendText(httpExchange, "Задача удалена!");
                return;
        }
        System.out.println("Неверный тип задачи - " + urlParts[2]);
        httpExchange.sendResponseHeaders(400, 0);
    }

    private void handleGetPrioritized(HttpExchange httpExchange) throws IOException {
        List<Task> prioritizedList = managers.getPrioritizedTasks();
        if (prioritizedList.isEmpty()) {
            System.out.println("Список пуст");
            httpExchange.sendResponseHeaders(404, 0);
        }
        sendText(httpExchange, gson.toJson(prioritizedList));
    }

    private void handleGetHistory(HttpExchange httpExchange) throws IOException {
        List<Task> history = managers.getHistory();
        if (history.isEmpty()) {
            System.out.println("История пуста");
            httpExchange.sendResponseHeaders(404, 0);
        }
        List<Integer> historyIds = history
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        sendText(httpExchange, gson.toJson(historyIds));
    }

    private void handleGetTasks(HttpExchange httpExchange, String type) throws IOException {
        switch (type) {
            case "task":
                sendText(httpExchange, gson.toJson(managers.getAllTasks()));
                return;
            case "subtask":
                sendText(httpExchange, gson.toJson(managers.getAllSubtask()));
                return;
            case "epic":
                sendText(httpExchange, gson.toJson(managers.getAllEpicTask()));
                return;
        }
        System.out.println("Неверный тип задачи - " + type);
        httpExchange.sendResponseHeaders(400, 0);
    }

    private void handleGetTaskByID(HttpExchange httpExchange, String[] urlParts) throws IOException {
        Optional<Integer> optionalId = getTaskId(urlParts);
        if (optionalId.isEmpty()) {
            System.out.println("Неверный id " + urlParts[urlParts.length - 1].split("=")[1]);
            httpExchange.sendResponseHeaders(400, 0);
            return;
        }
        int id = optionalId.get();
        switch (urlParts[2]) {
            case "task":
                sendText(httpExchange, gson.toJson(managers.getTaskById(id)));
                return;
            case "subtask":
                sendText(httpExchange, gson.toJson(managers.getSubtaskById(id)));
                return;
            case "epic":
                sendText(httpExchange, gson.toJson(managers.getEpicById(id)));
                return;
        }
        System.out.println("Неверный тип задачи - " + urlParts[2]);
        httpExchange.sendResponseHeaders(400, 0);
    }

    private Endpoint getEndpoint(String requestMethod, String url) {
        String[] urlParts = url.split("/");
        if (requestMethod.equals("GET")) {
            switch (url) {
                case "/tasks/":
                    return Endpoint.GET_PRIORITIZED;
                case "/tasks/history/":
                    return Endpoint.GET_HISTORY;
                case "/tasks/task/":
                case "/tasks/subtask/":
                case "/tasks/epic/":
                    return Endpoint.GET_TASKS;
            }
            if (urlParts[urlParts.length - 1].startsWith("?id")) {
                return Endpoint.GET_BY_ID;
            }
        }
        if (requestMethod.equals("DELETE")) {
            switch (url) {
                case "/tasks/task/":
                case "/tasks/subtask/":
                case "/tasks/epic/":
                    return Endpoint.DELETE_TASKS;
            }
            if (urlParts[urlParts.length - 1].startsWith("?id")) {
                return Endpoint.DELETE_BY_ID;
            }
        }
        if (requestMethod.equals("POST") && urlParts[1].equals("tasks")) {
            if (urlParts.length == 3) {
                return Endpoint.POST_TASK;
            } else {
                return Endpoint.POST_UPDATE;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private Optional<Integer> getTaskId(String[] urlParts) {
        try {
            return Optional.of(Integer.parseInt(urlParts[urlParts.length - 1].split("=")[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(1);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.managers.newTask(new Task("task1", "desTask1",
                22, null));
        httpTaskServer.managers.getTaskById(1);


        EpicTask epicTask = new EpicTask("Переезд", "новая квартира");
        httpTaskServer.managers.newEpicTask(epicTask);

        Subtask subtask1 = new Subtask("Вещи", "сложить все в коробки",
                5, Instant.ofEpochSecond(1717285397L), epicTask.getId());
        Subtask subtask2 = new Subtask("Грузчики", "найти помощников",
                4, Instant.ofEpochSecond(1726185697L), epicTask.getId());


        EpicTask epicTask2 = new EpicTask("Ужин", "подумать что приготовить на вечер");
        httpTaskServer.managers.newEpicTask(epicTask2);
        Subtask subtask3 = new Subtask("Магазин", "купить продукты для ужина",
                44, Instant.ofEpochSecond(1606185697L), epicTask2.getId());


        httpTaskServer.managers.newSubtask(subtask1);
        httpTaskServer.managers.newSubtask(subtask2);
        httpTaskServer.managers.newSubtask(subtask3);


        httpTaskServer.start();
    }
}
