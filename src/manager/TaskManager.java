package manager;

import models.EpicTask;
import models.StatusType;
import models.Subtask;
import models.Task;
import util.TaskNotFined;

import java.util.*;

public class TaskManager {

    private static int generateId = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epics = new HashMap<>();

    //находим все задачи и подзадачи
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subTasks.values());
    }

    public List<EpicTask> getAllEpicTask() {
        return new ArrayList<>(epics.values());
    }

    //удаляем все задачи
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtask() {
        subTasks.clear();
        epics.values().forEach(x -> x.getSubtasks().clear());
        epics.values().forEach(this::updateEpicStatus);
    }

    public void removeAllEpicTask() {
        epics.clear();
        subTasks.clear();
    }

    //создаем новую задачу
    public void newTask(Task task) {
        final int id = ++generateId;
        task.setId(id);
        tasks.put(id, task);
    }

    public void newSubtask(Subtask subtask) {
        final int id = ++generateId;
        subtask.setId(id);
        subTasks.put(id, subtask);
        epics.get(subtask.getEpicTask().getId()).getSubtasks().add(subtask);
        updateEpicStatus(epics.get(subtask.getEpicTask().getId()));
    }

    public void newEpicTask(EpicTask epicTask) {
        final int id = ++generateId;
        epicTask.setId(id);
        epics.put(epicTask.getId(), epicTask);
    }

    //находим все подзадачи
    public List<Subtask> getAllSubTaskByEpicID(int id) {
        try {
            return epics.get(id).getSubtasks();
        } catch (NullPointerException e) {
            throw new TaskNotFined("Эпик с идентификатором " + id + " не найден.");
        }
    }

    //удаляем задачу по идентификатору
    public void removeTaskById(int id) {
        exceptionHandler(id);
        tasks.values().removeIf(task -> task.equals(tasks.get(id)));
    }

    public void removeSubtaskById(int id) {
        try {
            epics.get(subTasks.get(id).getEpicTask()
                    .getId()).getSubtasks()
                    .removeIf(task -> task.equals(subTasks.get(id)));
            updateEpicStatus(epics.get(subTasks.get(id)
                    .getEpicTask().getId()));
            subTasks.remove(id);
        } catch (NullPointerException e) {
            throw new TaskNotFined("Задача с идентификатором " + id + " не найдена.");
        }
    }

    public void removeEpicTaskById(int id) {
        exceptionHandler(id);
        epics.remove(id);
        subTasks.entrySet().removeIf(task -> task.getValue().getEpicTask().getId() == id);
    }

    //находим задачу по идентификатору
    public Subtask getSubtaskById(int id) {
        exceptionHandler(id);
        return subTasks.get(id);
    }

    public EpicTask getEpicById(int id) {
        exceptionHandler(id);
        return epics.get(id);
    }

    public Task getTaskById(int id) {
        exceptionHandler(id);
        return tasks.get(id);
    }

    //обновляем задачу
    public void changeTask(int id, Task task, StatusType statusType) {
        exceptionHandler(id);
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            task.setStatus(statusType);
            final int idNew = ++generateId;
            task.setId(idNew);
            tasks.put(idNew, task);
        }
    }

    public void changeSubtask(int id, Subtask subtask, StatusType statusType) {
        exceptionHandler(id);
        subTasks.remove(id);
        subtask.setStatus(statusType);
        final int idNew = ++generateId;
        subtask.setId(idNew);
        subTasks.put(idNew, subtask);

        try {
            epics.get(subtask.getEpicTask().getId()).getSubtasks()
                    .removeIf(task1 -> task1.getId() == id);
            epics.get(subtask.getEpicTask().getId()).getSubtasks().add(subtask);
        } catch (NullPointerException e) {
            throw new TaskNotFined("Эпик с идентификатором " + id + " не найден.");
        }
        updateEpicStatus(subtask.getEpicTask());
    }

    public void changeEpicTask(int id, EpicTask epicTask) {
        exceptionHandler(id);
        epicTask.setSubtasks(epics.get(id).getSubtasks());
        final int idNew = ++generateId;
        epicTask.setId(idNew);
        subTasks.values().stream().filter(x -> x.getEpicTask().getId() == id)
                .forEach(x -> x.getEpicTask().setId(idNew));
        epics.remove(id);
        epics.put(idNew, epicTask);
        updateEpicStatus(epicTask);
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

    private void exceptionHandler(int id) {
        if (!tasks.containsKey(id) & !subTasks.containsKey(id) & !epics.containsKey(id)) {
            throw new TaskNotFined("Задача с идентификатором " + id + " не найдена.");
        }
    }
}
