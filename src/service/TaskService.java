package service;

import models.EpicTask;
import models.StatusType;
import models.Subtask;
import models.Task;
import util.TaskNotFined;

import java.util.*;

public class TaskService {

    public TreeMap<String, List<Task>> getAllTasks(HashMap<StatusType, List<Task>> kanban) {
        TreeMap<String, List<Task>> tasks = new TreeMap<>(Collections.reverseOrder());
        for (StatusType type : kanban.keySet()) {
            tasks.put(String.valueOf(type), kanban.get(type));
        }
        return tasks;
    }

    public void removeAllTasks(HashMap<StatusType, List<Task>> kanban) {
        kanban.clear();
        System.out.println("Все задачи удалены!");
    }

    public void newTask(HashMap<StatusType, List<Task>> kanban, Task task) {
        kanban.get(StatusType.NEW).add(task);
        System.out.println("Задача добавлена!");
    }

    public Collection<Subtask> getAllSubTask(HashMap<StatusType, List<Task>> kanban, int id) {
        for (List<Task> taskList : kanban.values()) {
            for (Task thisTask : taskList) {
                if (thisTask.getId() == id) {
                    if (thisTask instanceof EpicTask) {
                        return ((EpicTask) thisTask).getSubtasks();
                    }
                }
            }
        }
        throw new TaskNotFined("Задача с идентификатором " + id + " не найдена.");
    }

    public void changeTask(HashMap<StatusType, List<Task>> kanban, int id, Task task, StatusType statusType) {
        for (List<Task> taskList : kanban.values()) {
            for (Task thisTask : taskList) {
                if (thisTask.getId() == id) {
                    taskList.remove(thisTask);
                    if (!(task instanceof EpicTask)) {
                        task.setStatus(statusType);
                        kanban.get(statusType).add(task);
                    } else {
                        taskList.add(task);
                    }

                    System.out.println("Задача изменина!");
                    break;
                } else if (thisTask instanceof EpicTask) {
                    for (Subtask subtask : ((EpicTask) thisTask).getSubtasks()) {
                        if (id == subtask.getId()) {
                            ((EpicTask) thisTask).getSubtasks().remove(subtask);
                            subtask.setStatus(statusType);
                            ((EpicTask) thisTask).getSubtasks().add((Subtask) task);

                            int match = ((EpicTask) thisTask).getSubtasks().size();
                            for (int i = 0; i < ((EpicTask) thisTask).getSubtasks().size(); i++) {
                                if (((EpicTask) thisTask).getSubtasks().get(i).getStatus() != thisTask.getStatus()) {
                                    match--;
                                }
                            }
                            if (match == 0) {
                                thisTask.setStatus(statusType);
                                task = thisTask;
                                taskList.remove(thisTask);
                                kanban.get(statusType).add(task);
                                return;
                            }


                            System.out.println("Задача изменина!");
                            break;
                        }
                    }
                }
            }
        }
    }

    public void removeTaskById(HashMap<StatusType, List<Task>> kanban, int id) {
        for (List<Task> taskList : kanban.values()) {
            for (Task task : taskList) {
                if (task.getId() == id) {
                    taskList.remove(task);
                    System.out.println("Задача удалена!");
                    break;
                }
                if (task instanceof EpicTask) {
                    ((EpicTask) task).getSubtasks().removeIf(subtask -> id == subtask.getId());
                    System.out.println("Задача удалена!");
                    break;
                }
            }
        }
    }

    public Task getTaskById(HashMap<StatusType, List<Task>> kanban, int id) {
        for (List<Task> taskList : kanban.values()) {
            for (Task task : taskList) {
                if (task instanceof EpicTask) {
                    for (Subtask subtask : ((EpicTask) task).getSubtasks()) {
                        if (id == subtask.getId()) {
                            return subtask;
                        }
                    }
                }
                if (task.getId() == id) {
                    return task;
                }
            }
        }
        throw new TaskNotFined("Задача с идентификатором " + id + " не найдена.");
    }
}
