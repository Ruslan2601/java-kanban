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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;
    private final Gson gson = Managers.getGson();

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
        manager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(manager);
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        manager.newTask(task);
        httpTaskServer.start();
    }

    @AfterEach
    void stop() {
        httpTaskServer.stop();
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
        assertEquals(1, taskList.size(), "Не верное количество задач");
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
        assertEquals(2, taskList.size(), "Не верное количество задач");
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
        assertEquals(1, taskList.size(), "Не верное количество задач");
        assertEquals(epicTask, taskList.get(0), "Задачи не совпадают");
    }

}