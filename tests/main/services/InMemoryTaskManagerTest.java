package main.services;

import main.exceptions.TaskIntersectionsError;
import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private final EpicTask epicTask = new EpicTask(1, "EpicTask 1", "Household chores", StatusType.NEW);
    private final Subtask subtask1 = new Subtask(2, "Subtask 1", "Sport", 5,
            Instant.ofEpochMilli(1685999800000L), StatusType.NEW, 1);
    private final Subtask subtask2 = new Subtask(3, "Subtask 2", "Sport2", 15,
            Instant.ofEpochMilli(1686999900000L), StatusType.NEW, 1);
    private final Task task = new Task(4, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1685998800000L), StatusType.NEW);

    @BeforeEach
    void init() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void updateEpicStatus_Empty() {
        manager.newEpicTask(epicTask);

        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask(), "Список не пустой.");
        assertEquals(StatusType.NEW, manager.getEpicById(1).getStatus(), "Неверный статус задачи.");
    }

    @Test
    void updateEpicStatus_New() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);

        assertEquals(StatusType.NEW, manager.getEpicById(1).getStatus(), "Неверный статус задачи.");
    }

    @Test
    void updateEpicStatusDone() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        manager.changeSubtask(2, subtask1, StatusType.DONE);
        manager.changeSubtask(3, subtask2, StatusType.DONE);

        assertEquals(StatusType.DONE, manager.getEpicById(1).getStatus(), "Неверный статус задачи.");
    }

    @Test
    void updateEpicStatus_In_Progress() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        manager.changeSubtask(2, subtask1, StatusType.IN_PROGRESS);
        manager.changeSubtask(3, subtask2, StatusType.IN_PROGRESS);

        assertEquals(StatusType.IN_PROGRESS, manager.getEpicById(1).getStatus(), "Неверный статус задачи.");
    }

    @Test
    void updateEpicStatus_NewAndDone() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        manager.changeSubtask(3, subtask2, StatusType.DONE);

        assertEquals(StatusType.IN_PROGRESS, manager.getEpicById(1).getStatus(), "Неверный статус задачи.");
    }

    @Test
    void updateEpicTime_DurationSum() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        long duration = subtask1.getDuration() + subtask2.getDuration();

        assertEquals(duration, manager.getEpicById(1).getDuration(), "Время не совпадает.");
    }

    @Test
    void updateEpicTime_StartTimeMin() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        Long startTime = Math.min(subtask1.getStartTime().toEpochMilli(),
                subtask2.getStartTime().toEpochMilli());

        assertEquals(startTime, manager.getEpicById(1).getStartTime().toEpochMilli(), "Время не совпадает.");
    }

    @Test
    void updateEpicTime_EndTimeMax() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        Long endTime = Math.max(subtask1.getEndTime().toEpochMilli(),
                subtask2.getEndTime().toEpochMilli());

        assertEquals(endTime, manager.getEpicById(1).getEndTime().toEpochMilli(), "Время не совпадает.");
    }

    @Test
    void updateEpicTime_ThrowTaskIntersectionsError() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        subtask2.setStartTime(Instant.ofEpochMilli(1686999900000L));
        subtask1.setStartTime(Instant.ofEpochMilli(1686999900000L));
        manager.changeSubtask(3, subtask2, StatusType.DONE);

        final TaskIntersectionsError exception = assertThrows(
                TaskIntersectionsError.class,
                () -> manager.changeSubtask(2, subtask1, StatusType.DONE));

        assertEquals("Невозможно добавить задачу 2, найдено пересечение времени.", exception.getMessage());
    }

    @Test
    void printHistory_Size() {
        manager.newTask(task);
        manager.getTaskById(1);

        assertEquals(1, manager.historyManager.getHistory().size());
    }
}
