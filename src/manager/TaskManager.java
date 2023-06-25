package manager;

import models.StatusType;
import models.Subtask;
import models.Task;
import service.TaskService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class TaskManager {
    HashMap<StatusType, List<Task>> kanban;
    TaskService taskService;

    public TaskManager(HashMap<StatusType, List<Task>> kanban) {
        this.kanban = kanban;
        taskService = new TaskService();
    }

    //находим все задачи и подзадачи
    public TreeMap<String, List<Task>> getAllTasks() {
        return taskService.getAllTasks(kanban);
    }

    //удаляем все задачи
    public void removeAllTasks() {
        taskService.removeAllTasks(kanban);
    }

    //создаем новую задачу
    public void newTask(Task task) {
        taskService.newTask(kanban, task);
    }

    //находим все подзадачи
    public Collection<Subtask> getAllSubTask(int id) {
        return taskService.getAllSubTask(kanban, id);
    }

    //обновляем задачу
    public void changeTask(int id, Task task, StatusType statusType) {
        taskService.changeTask(kanban, id, task, statusType);
    }

    //удаляем задачу по идентификатору
    public void removeTaskById(int id) {
        taskService.removeTaskById(kanban, id);
    }

    //находим задачу по идентификатору
    public Task getTaskById(int id) {
        return taskService.getTaskById(kanban, id);
    }
}
