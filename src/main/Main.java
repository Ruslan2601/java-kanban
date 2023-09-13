package main;

import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.servers.HttpTaskServer;
import main.services.HttpTaskManager;
import main.services.Managers;
import main.util.StatusType;

import java.io.IOException;
import java.time.Instant;

public class Main {

    public static void main(String[] args) throws IOException {
        Managers.getDefaultKVServer().start();

        HttpTaskManager httpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        new HttpTaskServer(httpManager).start();

        httpManager.newTask(new Task("Task 1", "des1", 2,
                Instant.ofEpochMilli(1686603600000L), StatusType.NEW));
        httpManager.newTask(new Task("Task 2", "des2", 10,
                Instant.ofEpochMilli(1686790000000L), StatusType.IN_PROGRESS));

        EpicTask epic1 = new EpicTask("Epic 1", "epic des");
        httpManager.newEpicTask(epic1);
        httpManager.newSubtask(new Subtask("Subtask 1", "subtask des 1", 12,
                Instant.ofEpochMilli(1686803600000L),
                StatusType.IN_PROGRESS, 3));
        httpManager.newSubtask(new Subtask("Subtask 2", "subtask des 2", 9,
                Instant.ofEpochMilli(1686903600000L),
                StatusType.NEW, 3));
        httpManager.newSubtask(new Subtask("Subtask 3", "subtask des 3", 11,
                Instant.ofEpochMilli(1687003600000L),
                StatusType.DONE, 3));

        httpManager.getTaskById(1);
        httpManager.getEpicById(3);
        httpManager.getSubtaskById(4);


        System.out.println("------------------------------------------------------");


        httpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        httpManager.load();

        System.out.println(httpManager.getAllEpicTask());
        System.out.println(httpManager.getHistory());
        System.out.println(httpManager.getPrioritizedTasks());
    }
}
