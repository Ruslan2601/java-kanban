package main.interfaces;

import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusType;

import java.util.List;

public interface TaskManager {
    //������� ��� ������ � ���������
    List<Task> getAllTasks();

    List<Subtask> getAllSubtask();

    List<EpicTask> getAllEpicTask();

    //������� ��� ������
    void removeAllTasks();

    void removeAllSubtask();

    void removeAllEpicTask();

    //������� ����� ������
    Task newTask(Task task);

    Subtask newSubtask(Subtask subtask);

    EpicTask newEpicTask(EpicTask epicTask);

    //������� ��� ���������
    List<Subtask> getAllSubTaskByEpicID(int id);

    //������� ������ �� ��������������
    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicTaskById(int id);

    //������� ������ �� ��������������
    Subtask getSubtaskById(int id);

    EpicTask getEpicById(int id);

    Task getTaskById(int id);

    //��������� ������
    void changeTask(int id, Task task, StatusType statusType);

    void changeSubtask(int id, Subtask subtask, StatusType statusType);

    void changeEpicTask(int id, EpicTask epicTask);

    void printHistory();
}
