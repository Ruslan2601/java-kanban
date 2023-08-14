package main.interfaces;

import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusType;

import java.util.List;

public interface TaskManager {
    //находим все задачи и подзадачи
    List<Task> getAllTasks();

    List<Subtask> getAllSubtask();

    List<EpicTask> getAllEpicTask();

    //удаляем все задачи
    void removeAllTasks();

    void removeAllSubtask();

    void removeAllEpicTask();

    //создаем новую задачу
    Task newTask(Task task);

    Subtask newSubtask(Subtask subtask);

    EpicTask newEpicTask(EpicTask epicTask);

    //находим все подзадачи
    List<Subtask> getAllSubTaskByEpicID(int id);

    //удаляем задачу по идентификатору
    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicTaskById(int id);

    //находим задачу по идентификатору
    Subtask getSubtaskById(int id);

    EpicTask getEpicById(int id);

    Task getTaskById(int id);

    //обновляем задачу
    void changeTask(int id, Task task, StatusType statusType);

    void changeSubtask(int id, Subtask subtask, StatusType statusType);

    void changeEpicTask(int id, EpicTask epicTask);

    void printHistory();
}
