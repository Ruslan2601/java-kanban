package manager;

import models.EpicTask;
import models.StatusType;
import models.Subtask;
import models.Task;
import util.TaskNotFined;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    private final HashMap<StatusType, List<Task>> tasks = new HashMap<>();
    private final HashMap<StatusType, List<Subtask>> subTasks = new HashMap<>();
    private final HashMap<StatusType, List<EpicTask>> epics = new HashMap<>();

    public TaskManager(List<Task> tasksList, List<Subtask> subtaskList, List<EpicTask> epicTasks) {
        this.tasks.put(StatusType.NEW, tasksList);
        this.tasks.put(StatusType.IN_PROGRESS, new ArrayList<>());
        this.tasks.put(StatusType.DONE, new ArrayList<>());

        this.subTasks.put(StatusType.NEW, subtaskList);
        this.subTasks.put(StatusType.IN_PROGRESS, new ArrayList<>());
        this.subTasks.put(StatusType.DONE, new ArrayList<>());

        this.epics.put(StatusType.NEW, epicTasks);
        this.epics.put(StatusType.IN_PROGRESS, new ArrayList<>());
        this.epics.put(StatusType.DONE, new ArrayList<>());
    }

    //находим все задачи и подзадачи
    public List<Task> getAllTasks() {
        return tasks.entrySet().stream()
                .flatMap(o -> o.getValue().stream())
                .collect(Collectors.toList());
    }

    public List<Subtask> getAllSubtask() {
        return subTasks.entrySet().stream()
                .flatMap(o -> o.getValue().stream())
                .collect(Collectors.toList());
    }

    public List<EpicTask> getAllEpicTask() {
        return epics.entrySet().stream()
                .flatMap(o -> o.getValue().stream())
                .collect(Collectors.toList());
    }

    //удаляем все задачи
    public void removeAllTasks() {
        for (StatusType statusType : tasks.keySet()) {
            tasks.get(statusType).clear();
        }
    }

    public void removeAllSubtask() {
        for (StatusType statusType : subTasks.keySet()) {
            subTasks.get(statusType).clear();
        }
    }

    public void removeAllEpicTask() {
        for (StatusType statusType : epics.keySet()) {
            epics.get(statusType).clear();
            subTasks.get(statusType).clear();
        }
    }

    //создаем новую задачу
    public void newTask(Task task) {
        tasks.get(StatusType.NEW).add(task);
    }

    public void newSubtask(Subtask subtask) {
        subTasks.get(StatusType.NEW).add(subtask);
        for (List<EpicTask> epicTaskList : epics.values()) {
            for (EpicTask epicTask : epicTaskList) {
                if (subtask.getEpicTask().getId() == epicTask.getId()) {
                    epicTask.getSubtasks().add(subtask);
                }
            }
        }
    }

    public void newEpicTask(EpicTask epicTask) {
        epics.get(StatusType.NEW).add(epicTask);
    }

    //находим все подзадачи
    public List<Subtask> getAllSubTask(int id) {
        List<Subtask> subtaskList = subTasks.entrySet().stream()
                .flatMap(o -> o.getValue().stream())
                .collect(Collectors.toList()).stream()
                .filter(x -> x.getEpicTask().getId() == id)
                .collect(Collectors.toList());
        if (subtaskList.isEmpty()) {
            throw new TaskNotFined("Эпик с идентификатором " + id + " не найден.");
        }
        return subtaskList;
    }

    //удаляем задачу по идентификатору
    public void removeTaskById(int id) {
        for (List<Task> taskList : tasks.values()) {
            taskList.removeIf(task -> task.equals(getTaskById(id)));
        }
    }

    public void removeSubtaskById(int id) {
        for (List<EpicTask> epicTaskList : epics.values()) {
            for (EpicTask epicTask : epicTaskList) {
                epicTask.getSubtasks().removeIf(subTask -> subTask.equals(getSubtaskById(id)));
                StatusType status = epicTask.getStatus();
                updateEpicStatus(epicTask);
                if (!(status.equals(epicTask.getStatus()))) {
                    epicTaskList.remove(epicTask);
                    epics.get(epicTask.getStatus()).add(epicTask);
                    break;
                }
            }
        }
        for (List<Subtask> subtaskList : subTasks.values()) {
            subtaskList.removeIf(subTask -> subTask.equals(getSubtaskById(id)));
        }

    }

    public void removeEpicTaskById(int id) {
        for (List<Subtask> subtaskList : subTasks.values()) {
            subtaskList.removeIf(subTask -> subTask.getEpicTask().equals(getEpicById(id)));
        }
        for (List<EpicTask> epicTaskList : epics.values()) {
            epicTaskList.removeIf(epic -> epic.equals(getEpicById(id)));
        }
    }

    //находим задачу по идентификатору
    public Subtask getSubtaskById(int id) {
        return subTasks.entrySet().stream()
                .flatMap(o -> o.getValue().stream())
                .filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    public EpicTask getEpicById(int id) {
        return epics.entrySet().stream()
                .flatMap(o -> o.getValue().stream())
                .filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    public Task getTaskById(int id) {
        return tasks.entrySet().stream()
                .flatMap(o -> o.getValue().stream())
                .filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    //обновляем задачу
    public void changeTask(int id, Task task, StatusType statusType) {
        for (List<Task> taskList : tasks.values()) {
            boolean b = taskList.removeIf(task1 -> task1.getId() == id);
            if (b) {
                task.setStatus(statusType);
                tasks.get(statusType).add(task);
                break;
            }
        }
    }

    public void changeSubtask(int id, Subtask subtask, StatusType statusType) {
        for (List<Subtask> taskList : subTasks.values()) {
            boolean b = taskList.removeIf(task1 -> task1.getId() == id);
            if (b) {
                subtask.setStatus(statusType);
                subTasks.get(statusType).add(subtask);
                break;
            }
        }
        for (List<EpicTask> taskList : epics.values()) {
            for (EpicTask epicTask : taskList) {
                boolean b = epicTask.getSubtasks().removeIf(task1 -> task1.getId() == id);
                if (b) {
                    epicTask.getSubtasks().add(subtask);
                    StatusType status = epicTask.getStatus();
                    updateEpicStatus(epicTask);
                    if (!(status.equals(epicTask.getStatus()))) {
                        taskList.remove(epicTask);
                        epics.get(epicTask.getStatus()).add(epicTask);
                    }
                    break;
                }
            }
        }
    }

    public void changeEpicTask(int id, EpicTask epicTask) {
        for (List<EpicTask> taskList : epics.values()) {
            for (EpicTask task : taskList) {
                if (id == task.getId()) {
                    task.setTaskName(epicTask.getTaskName());
                    task.setDescription(epicTask.getDescription());
                    break;
                }
            }
        }
    }

    private void updateEpicStatus(EpicTask epicTask) {
        boolean done = epicTask.getSubtasks().stream().anyMatch(x -> x.getStatus().equals(StatusType.DONE));
        boolean inProgress = epicTask.getSubtasks().stream().anyMatch(x -> x.getStatus().equals(StatusType.IN_PROGRESS));

        int match = epicTask.getSubtasks().size();
        for (int i = 0; i < epicTask.getSubtasks().size(); i++) {
            if (epicTask.getSubtasks().get(i).getStatus().equals(StatusType.DONE)) {
                match--;
            }
        }

        if (epicTask.getSubtasks().size() == 0 | !done & !inProgress) {
            epicTask.setStatus(StatusType.NEW);
        } else if (match == 0) {
            epicTask.setStatus(StatusType.DONE);
        } else {
            epicTask.setStatus(StatusType.IN_PROGRESS);
        }
    }
}
