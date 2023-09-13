package main.services;

import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.servers.KVServer;
import main.util.StatusType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;


class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static KVServer server;

    private final Task task = new Task(1, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1685998800000L), StatusType.NEW);
    private final EpicTask epicTask = new EpicTask(2, "EpicTask 1", "Household chores", StatusType.NEW);
    private final Subtask subtask1 = new Subtask(3, "Subtask 1", "Sport", 5,
            Instant.ofEpochMilli(1685989800000L), StatusType.NEW, 2);
    private final Subtask subtask2 = new Subtask(4, "Subtask 2", "Sport2", 15,
            Instant.ofEpochMilli(1685999900001L), StatusType.NEW, 2);

    @BeforeEach
    void setManager() {
        manager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
    }

    @BeforeAll
    static void startServer() throws IOException {
        server = Managers.getDefaultKVServer();
        server.start();
    }

    @Test
    void shouldLoadFromServer() {
        manager.newTask(task);
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        manager.getTaskById(task.getId());
        manager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        manager.load();

        assertEquals(3, manager.getPrioritizedTasks().size(), "Неверное количество задач");
        assertEquals(1, manager.getHistory().size(), "Неверное количество задач");
        assertEquals(task.toStringFromFile(), manager.getTaskById(task.getId()).toStringFromFile(), "Задача не совпадает");
        assertEquals(epicTask.toStringFromFile(), manager.getEpicById(epicTask.getId()).toStringFromFile(), "Задача не совпадает");
        assertEquals(subtask1.toStringFromFile(), manager.getSubtaskById(subtask1.getId()).toStringFromFile(), "Задача не совпадает");
        assertEquals(subtask2.toStringFromFile(), manager.getSubtaskById(subtask2.getId()).toStringFromFile(), "Задача не совпадает");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

}