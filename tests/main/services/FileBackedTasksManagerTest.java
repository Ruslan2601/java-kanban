package main.services;

import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final Path path = Path.of("tests/resources/history.csv");
    private final File file = new File(String.valueOf(path));

    private final Task task = new Task(1, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1685998800000L), StatusType.NEW);
    private final EpicTask epicTask = new EpicTask(2, "EpicTask 1", "Household chores", StatusType.NEW);
    private final Subtask subtask1 = new Subtask(3, "Subtask 1", "Sport", 5,
            Instant.ofEpochMilli(1685999800000L), StatusType.NEW, 2);
    private final Subtask subtask2 = new Subtask(4, "Subtask 2", "Sport2", 15,
            Instant.ofEpochMilli(1685999900000L), StatusType.NEW, 2);

    @BeforeEach
    void init() {
        manager = new FileBackedTasksManager(path.toString());
    }

    @Test
    void save_loadFromFile_shouldSaveAndLoadCorrectly() {
        Task thisTask = manager.newTask(task);
        EpicTask thisEpic = manager.newEpicTask(epicTask);
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);
        List<Task> listOfTasks = new ArrayList<>(manager.getAllTasks());
        List<EpicTask> listOfEpics = new ArrayList<>(manager.getAllEpicTask());

        assertEquals(List.of(thisTask), listOfTasks, "Задачи не совпадают.");
        assertEquals(List.of(thisEpic), listOfEpics, "Задачи не совпадают.");
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask(), "Список не пустой.");
    }

    @Test
    void save_loadFromFile_HistoryNotEmpty() {
        EpicTask thisEpic = manager.newEpicTask(epicTask);
        manager.getEpicById(epicTask.getId());
        FileBackedTasksManager.loadFromFile(file);

        assertEquals(List.of(thisEpic), manager.historyManager.getHistory(), "Задачи не совпадают.");
    }

    @Test
    void save_loadFromFile_HistoryEmpty() {
        FileBackedTasksManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, manager.historyManager.getHistory(), "Список не пустой.");
    }
}