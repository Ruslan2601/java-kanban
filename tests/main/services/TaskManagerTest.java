package main.services;

import main.exceptions.TaskNotFined;
import main.interfaces.TaskManager;
import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    private final EpicTask epicTask = new EpicTask(1, "EpicTask 1", "Household chores", StatusType.NEW);
    private final Subtask subtask1 = new Subtask(2, "Subtask 1", "Sport", 5,
            Instant.ofEpochMilli(1685999800000L), StatusType.NEW, 1);
    private final Subtask subtask2 = new Subtask(3, "Subtask 2", "Sport2", 15,
            Instant.ofEpochMilli(1686999900000L), StatusType.NEW, 1);
    private final Task task = new Task(4, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1685998800000L), StatusType.NEW);

    @Test
    void getAllTasks_Empty_NotNull_Size_Equals() {
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
        manager.newTask(task);

        assertEquals(List.of(task), manager.getAllTasks(), "Задачи не совпадают.");
        assertNotNull(manager.getAllTasks(), "Задачи на возвращаются.");
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач.");
    }

    @Test
    void getAllSubtask_Empty_NotNull_Size_Equals() {
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask());
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);

        assertEquals(List.of(subtask1), manager.getAllSubtask(), "Задачи не совпадают.");
        assertNotNull(manager.getAllSubtask(), "Задачи на возвращаются.");
        assertEquals(1, manager.getAllSubtask().size(), "Неверное количество задач.");
    }

    @Test
    void getAllEpicTask_Empty_NotNull_Size_Equals() {
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpicTask());
        manager.newEpicTask(epicTask);

        assertEquals(List.of(epicTask), manager.getAllEpicTask(), "Задачи не совпадают.");
        assertNotNull(manager.getAllEpicTask(), "Задачи на возвращаются.");
        assertEquals(1, manager.getAllEpicTask().size(), "Неверное количество задач.");
    }

    @Test
    void removeAllTasks_Empty() {
        manager.newTask(task);
        manager.removeAllTasks();

        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks(), "Список не пустой.");
    }

    @Test
    void removeAllSubtask_Empty() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.removeAllSubtask();

        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask(), "Список не пустой.");
    }

    @Test
    void removeAllEpicTask_Empty() {
        manager.newEpicTask(epicTask);
        manager.removeAllEpicTask();

        assertEquals(Collections.EMPTY_LIST, manager.getAllEpicTask(), "Список не пустой.");
    }

    @Test
    void newTask_Correct_Size() {
        manager.newTask(task);

        assertEquals(task, manager.getTaskById(1), "Задачи не совпадают.");
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач.");
    }

    @Test
    void newSubtask_Correct_Size_HaveEpic() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);

        assertEquals(subtask1, manager.getSubtaskById(2), "Задачи не совпадают.");
        assertEquals(1, manager.getAllSubtask().size(), "Неверное количество задач.");
        assertEquals(epicTask, manager.getEpicById(manager.getSubtaskById(2).getEpicId()), "Задачи не совпадают.");

    }

    @Test
    void newEpicTask_Correct_Size() {
        manager.newEpicTask(epicTask);

        assertEquals(epicTask, manager.getEpicById(1), "Задачи не совпадают.");
        assertEquals(1, manager.getAllEpicTask().size(), "Неверное количество задач.");
    }

    @Test
    void getAllSubTaskByEpicID_Correct_Size() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);

        assertEquals(List.of(subtask1, subtask2), manager.getAllSubTaskByEpicID(1), "Задачи не совпадают.");
        assertEquals(2, manager.getAllSubtask().size(), "Неверное количество задач.");
    }

    @Test
    void getAllSubTaskByEpicID_ThrowErrorId() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);
        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.getAllSubTaskByEpicID(4));

        assertEquals("Задача с идентификатором " + 4 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void getAllSubTaskByEpicID_ErrorId() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.newSubtask(subtask2);

        assertEquals(Collections.EMPTY_LIST, manager.getAllSubTaskByEpicID(2), "Список не пуст");
    }

    @Test
    void removeTaskById_Correct() {
        manager.newTask(task);
        manager.removeTaskById(1);

        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks(), "Список не пуст");
    }

    @Test
    void removeTaskById_WrongId() {
        manager.newTask(task);

        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.removeTaskById(2));

        assertEquals("Задача с идентификатором " + 2 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void removeSubtaskById_Correct() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        manager.removeSubtaskById(2);

        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask(), "Список не пуст");
    }

    @Test
    void removeSubtaskById_WrongId() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);

        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.removeSubtaskById(3));

        assertEquals("Задача с идентификатором " + 3 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void removeEpicTaskById_Correct() {
        manager.newEpicTask(epicTask);
        manager.removeEpicTaskById(1);

        assertEquals(Collections.EMPTY_LIST, manager.getAllEpicTask(), "Список не пуст");
    }

    @Test
    void removeEpicTaskById_WrongId() {
        manager.newEpicTask(epicTask);

        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.removeEpicTaskById(3));

        assertEquals("Задача с идентификатором " + 3 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void getSubtaskById_Correct() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);

        assertEquals(subtask1, manager.getSubtaskById(2), "Задачи не совпадают.");
    }

    @Test
    void getSubtaskById_WrongId() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);

        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.getSubtaskById(3));

        assertEquals("Задача с идентификатором " + 3 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void getEpicById_Correct() {
        manager.newEpicTask(epicTask);

        assertEquals(epicTask, manager.getEpicById(1), "Задачи не совпадают.");
    }

    @Test
    void getEpicById_WrongId() {
        manager.newEpicTask(epicTask);

        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.getEpicById(3));

        assertEquals("Задача с идентификатором " + 3 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void getTaskById_Correct() {
        manager.newTask(task);

        assertEquals(task, manager.getTaskById(1), "Задачи не совпадают.");
    }

    @Test
    void getTaskById_WrongId() {
        manager.newTask(task);

        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.getTaskById(3));

        assertEquals("Задача с идентификатором " + 3 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void changeTask_Correct() {
        manager.newTask(task);
        task.setTaskName("Task 13");
        task.setDuration(55);
        task.setStatus(StatusType.DONE);
        manager.changeTask(1, task, task.getStatus());

        assertEquals(task, manager.getTaskById(1), "Задачи не совпадают.");
    }

    @Test
    void changeTask_WrongID() {
        manager.newTask(task);
        task.setTaskName("Task 13");
        task.setDuration(55);
        task.setStatus(StatusType.DONE);


        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.changeTask(3, task, task.getStatus()));

        assertEquals("Задача с идентификатором " + 3 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void changeSubtask_Correct() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        subtask1.setTaskName("Task 13");
        subtask1.setDuration(55);
        subtask1.setStatus(StatusType.DONE);
        manager.changeSubtask(2, subtask1, subtask1.getStatus());

        assertEquals(subtask1, manager.getSubtaskById(2), "Задачи не совпадают.");
    }

    @Test
    void changeSubtask_WrongID() {
        manager.newEpicTask(epicTask);
        manager.newSubtask(subtask1);
        subtask1.setTaskName("Subtask 13");
        subtask1.setDuration(55);
        subtask1.setStatus(StatusType.DONE);
        manager.changeSubtask(2, subtask1, subtask1.getStatus());

        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.changeSubtask(3, subtask1, subtask1.getStatus()));

        assertEquals("Задача с идентификатором " + 3 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }

    @Test
    void changeEpicTask_Correct() {
        manager.newEpicTask(epicTask);
        epicTask.setTaskName("Epic 13");
        epicTask.setDescription("Desc 13");
        manager.changeEpicTask(1, epicTask);

        assertEquals(epicTask, manager.getEpicById(1), "Задачи не совпадают.");
    }

    @Test
    void changeEpicTask_WrongID() {
        manager.newEpicTask(epicTask);
        epicTask.setTaskName("Epic 13");
        epicTask.setDescription("Desc 13");
        manager.changeEpicTask(1, epicTask);

        final TaskNotFined exception = assertThrows(
                TaskNotFined.class,
                () -> manager.changeEpicTask(3, epicTask));

        assertEquals("Задача с идентификатором " + 3 + " не найдена.", exception.getMessage(), "Ошибка не выходит");
    }
}