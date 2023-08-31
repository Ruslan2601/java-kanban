package main.services;

import main.interfaces.HistoryManager;
import main.models.Task;
import main.util.StatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    protected HistoryManager historyManager;

    private final Task task = new Task(1, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1685998800000L), StatusType.NEW);
    private final Task task2 = new Task(2, "Task 2", "Sport", 0,
            Instant.ofEpochMilli(1686603600000L), StatusType.IN_PROGRESS);
    private final Task task3 = new Task(3, "Task 3", "Household chores", 0,
            Instant.ofEpochMilli(1686085200000L), StatusType.DONE);

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTaskToHistory() {
        historyManager.addHistory(task);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(List.of(task), historyManager.getHistory());
    }

    @Test
    void removeTaskInHistory() {
        historyManager.addHistory(task);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        historyManager.remove(task2.getId());
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(List.of(task, task3), historyManager.getHistory());
    }

    @Test
    void getHistory() {
        assertEquals(0, historyManager.getHistory().size());
        historyManager.addHistory(task);
        assertEquals(1, historyManager.getHistory().size());
    }
}