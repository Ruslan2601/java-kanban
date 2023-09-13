package main.servers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.interfaces.TaskManager;
import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.services.Managers;
import main.util.StatusType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;
    private final Gson gson = Managers.getGson();
    private KVServer kvServer;
    private TaskManager manager;

    private final EpicTask epicTask = new EpicTask(1, "EpicTask 1", "Household chores", StatusType.NEW);
    private final Subtask subtask1 = new Subtask(2, "Subtask 1", "Sport", 5,
            Instant.ofEpochMilli(1685999800000L), StatusType.NEW, 1);
    private final Subtask subtask2 = new Subtask(3, "Subtask 2", "Sport2", 15,
            Instant.ofEpochMilli(1686999900000L), StatusType.NEW, 1);
    private final Task task = new Task(4, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1685998800000L), StatusType.NEW);

    @BeforeEach
    void init() throws IOException {
        kvServer = Managers.getDefaultKVServer();
        kvServer.start();
        manager = Managers.getDefault("http://localhost:8078");
        httpTaskServer = new HttpTaskServer(manager);
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        manager.newTask(task);
        manager.getTaskById(4);
        httpTaskServer.start();
    }

    @AfterEach
    void stop() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void getTasks_NonNUll_Size_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");

        Type type = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> taskList = gson.fromJson(response.body(), type);

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(1, taskList.size(), "Неверное количество задач");
        assertEquals(task, taskList.get(0), "Задачи не совпадают");
    }

    @Test
    void getSubtasks_NonNUll_Size_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");

        Type type = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> taskList = gson.fromJson(response.body(), type);

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(2, taskList.size(), "Неверное количество задач");
        assertEquals(subtask1, taskList.get(0), "Задачи не совпадают");
    }

    @Test
    void getEpicTasks_NonNUll_Size_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");

        Type type = new TypeToken<ArrayList<EpicTask>>() {
        }.getType();
        List<EpicTask> taskList = gson.fromJson(response.body(), type);

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(1, taskList.size(), "Неверное количество задач");
        assertEquals(epicTask, taskList.get(0), "Задачи не совпадают");
    }

    @Test
    void getEpicTaskById_NonNUll_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");

        Type type = new TypeToken<EpicTask>() {
        }.getType();
        EpicTask epicTask1 = gson.fromJson(response.body(), type);

        assertNotNull(epicTask1, "Задачи не возвращаются");
        assertEquals(epicTask, epicTask1, "Задачи не совпадают");
    }

    @Test
    void getSubtaskTaskById_NonNUll_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");

        Type type = new TypeToken<Subtask>() {
        }.getType();
        Subtask subtask = gson.fromJson(response.body(), type);

        assertNotNull(subtask, "Задачи не возвращаются");
        assertEquals(subtask1, subtask, "Задачи не совпадают");
    }

    @Test
    void getTaskById_NonNUll_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");

        Type type = new TypeToken<Task>() {
        }.getType();
        Task task1 = gson.fromJson(response.body(), type);

        assertNotNull(task1, "Задачи не возвращаются");
        assertEquals(task, task1, "Задачи не совпадают");
    }

    @Test
    void getHistory_NonNUll_Equals_Size() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");

        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> listIds = gson.fromJson(response.body(), type);

        assertNotNull(listIds, "Задачи не возвращаются");
        assertEquals(1, listIds.size(), "Неверное количество задач");
        assertEquals(task.getId(), listIds.get(0), "Задачи не совпадают");
    }

    @Test
    void getPrioritized_NonNUll_Equals_Size() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");

        Type type = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> tasksList = gson.fromJson(response.body(), type);

        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(3, tasksList.size(), "Неверное количество задач");
        assertEquals(task, tasksList.get(0), "Задачи не совпадают");
    }

    @Test
    void deleteSubTaskById_Empty_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код");

        uri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask(), "Список не пуст");
    }

    @Test
    void deleteTaskById_Empty_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create("http://localhost:8080/tasks/task/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks(), "Список не пуст");
    }

    @Test
    void deleteEpicTaskById_Empty_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpicTask(), "Список не пуст");
    }

    @Test
    void deleteAllEpicTasks_Empty_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpicTask(), "Список не пуст");
    }

    @Test
    void deleteAllTasks_Empty_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks(), "Список не пуст");
    }

    @Test
    void deleteAllSubTasks_Empty_Equals() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask(), "Список не пуст");
    }

    @Test
    void postTask_Equals() throws IOException, InterruptedException {
        Task task1 = new Task(5, "Task 1", "Groceries", 0,
                Instant.ofEpochMilli(1686998800000L), StatusType.NEW);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task1));
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(task1, manager.getTaskById(5), "Задачи не совпадают");
    }

    @Test
    void postSubtask_Equals() throws IOException, InterruptedException {
        Subtask subtask3 = new Subtask(5, "Subtask 3", "Sport3", 15,
                Instant.ofEpochMilli(1687999900000L), StatusType.NEW, 1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3));
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(subtask3, manager.getSubtaskById(5), "Задачи не совпадают");
    }

    @Test
    void postEpic_Equals() throws IOException, InterruptedException {
        EpicTask epicTask1 = new EpicTask(5, "EpicTask 2", "Household chores", StatusType.NEW);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epicTask1));
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(epicTask1, manager.getEpicById(5), "Задачи не совпадают");
    }

    @Test
    void postUpdateEpic_Equals() throws IOException, InterruptedException {
        EpicTask epicTask1 = new EpicTask("EpicTask 2", "Household chores");

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=1");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epicTask1));
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(epicTask1.getTaskName(), manager.getEpicById(1).getTaskName(), "Задачи не совпадают");
    }

    @Test
    void postUpdateTask_Equals() throws IOException, InterruptedException {
        Task task1 = new Task(4, "Task 1", "Groceries", 0,
                Instant.ofEpochMilli(1686998800000L), StatusType.DONE);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=4");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task1));
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(task1.getStatus(), manager.getTaskById(4).getStatus(), "Задачи не совпадают");
    }

    @Test
    void postUpdateSubTask_Equals() throws IOException, InterruptedException {
        Subtask subtask3 = new Subtask(2, "Subtask 3", "Sport3", 15,
                Instant.ofEpochMilli(1687999900000L), StatusType.DONE, 1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3));
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(subtask3.getStatus(), manager.getSubtaskById(2).getStatus(), "Задачи не совпадают");
        assertEquals(StatusType.IN_PROGRESS, manager.getEpicById(1).getStatus(), "Задачи не совпадают");
    }
}