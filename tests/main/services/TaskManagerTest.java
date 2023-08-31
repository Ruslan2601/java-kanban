package main.services;

import main.interfaces.HistoryManager;
import main.interfaces.TaskManager;
import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected HistoryManager historyManager;

    private final Task task = new Task(1, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1685998800000L), StatusType.NEW);
    private final EpicTask epicTask = new EpicTask(2, "EpicTask 1", "Household chores", StatusType.NEW);
    private final Subtask subtask1 = new Subtask(3, "Subtask 1", "Sport", 5,
            Instant.ofEpochMilli(1685999800000L), StatusType.NEW, 2);
    private final Subtask subtask2 = new Subtask(4, "Subtask 2", "Sport2", 15,
            Instant.ofEpochMilli(1685999900000L), StatusType.NEW, 2);


    @Test
    void getAllTasks() {
    }

    @Test
    void getAllSubtask() {
    }

    @Test
    void getAllEpicTask() {
    }

    @Test
    void removeAllTasks() {
    }

    @Test
    void removeAllSubtask() {
    }

    @Test
    void removeAllEpicTask() {
    }

    @Test
    void newTask() {
    }

    @Test
    void newSubtask() {
    }

    @Test
    void newEpicTask() {
    }

    @Test
    void getAllSubTaskByEpicID() {
    }

    @Test
    void removeTaskById() {
    }

    @Test
    void removeSubtaskById() {
    }

    @Test
    void removeEpicTaskById() {
    }

    @Test
    void getSubtaskById() {
    }

    @Test
    void getEpicById() {
    }

    @Test
    void getTaskById() {
    }

    @Test
    void changeTask() {
    }

    @Test
    void changeSubtask() {
    }

    @Test
    void changeEpicTask() {
    }

    @Test
    void printHistory() {
    }
}