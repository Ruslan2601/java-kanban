package services;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.EpicTask;
import util.StatusType;
import models.Subtask;
import models.Task;
import util.TaskNotFined;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private static int generateId = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epics = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    //находим все задачи и подзадачи
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<EpicTask> getAllEpicTask() {
        return new ArrayList<>(epics.values());
    }

    //удаляем все задачи
    @Override
    public void removeAllTasks() {
        List<Integer> ids = new ArrayList(tasks.keySet());
        for (Integer id : ids) {
            tasks.remove(id);
            ;
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllSubtask() {
        List<Integer> ids = new ArrayList(subTasks.keySet());
        for (Integer id : ids) {
            subTasks.remove(id);
            ;
            historyManager.remove(id);
        }
        epics.values().forEach(x -> x.getSubtasks().clear());
        epics.values().forEach(this::updateEpicStatus);
    }

    @Override
    public void removeAllEpicTask() {
        removeAllSubtask();
        List<Integer> ids = new ArrayList(epics.keySet());
        for (Integer id : ids) {
            epics.remove(id);
            historyManager.remove(id);
        }
        epics.clear();
    }

    //создаем новую задачу
    @Override
    public Task newTask(Task task) {
        final int id = ++generateId;
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Subtask newSubtask(Subtask subtask) {
        final int id = ++generateId;
        subtask.setId(id);
        subTasks.put(id, subtask);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask.getId());
        updateEpicStatus(epics.get(subtask.getEpicId()));
        return subtask;
    }

    @Override
    public EpicTask newEpicTask(EpicTask epicTask) {
        final int id = ++generateId;
        epicTask.setId(id);
        epics.put(epicTask.getId(), epicTask);
        return epicTask;
    }

    //находим все подзадачи
    @Override
    public List<Subtask> getAllSubTaskByEpicID(int id) {
        exceptionHandler(id);
        return subTasks.values().stream().filter(x -> x.getEpicId() == id).collect(Collectors.toList());
    }

    //удаляем задачу по идентификатору
    @Override
    public void removeTaskById(int id) {
        exceptionHandler(id);
        tasks.values().removeIf(task -> task.equals(tasks.get(id)));
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        try {
            epics.get(subTasks.get(id).getEpicId()).getSubtasks()
                    .removeIf(task -> task == id);
            updateEpicStatus(epics.get(subTasks.get(id)
                    .getEpicId()));
            subTasks.remove(id);
        } catch (NullPointerException e) {
            throw new TaskNotFined("Задача с идентификатором " + id + " не найдена.");
        }
        historyManager.remove(id);
    }

    @Override
    public void removeEpicTaskById(int id) {
        exceptionHandler(id);
        List<Integer> ids = new ArrayList(epics.get(id).getSubtasks());
        for (Integer idSubtask : ids) {
            historyManager.remove(idSubtask);
        }
        epics.remove(id);
        subTasks.entrySet().removeIf(task -> task.getValue().getEpicId() == id);
        historyManager.remove(id);
    }

    //находим задачу по идентификатору
    @Override
    public Subtask getSubtaskById(int id) {
        exceptionHandler(id);
        historyManager.addHistory(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public EpicTask getEpicById(int id) {
        exceptionHandler(id);
        historyManager.addHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task getTaskById(int id) {
        exceptionHandler(id);
        historyManager.addHistory(tasks.get(id));
        return tasks.get(id);
    }

    //обновляем задачу
    @Override
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

    @Override
    public void changeSubtask(int id, Subtask subtask, StatusType statusType) {
        exceptionHandler(id);
        subTasks.get(id).setEpicId(subtask.getEpicId());
        subTasks.get(id).setTaskName(subtask.getTaskName());
        subTasks.get(id).setDescription(subtask.getDescription());
        subTasks.get(id).setStatus(statusType);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public void changeEpicTask(int id, EpicTask epicTask) {
        exceptionHandler(id);
        epics.get(id).setTaskName(epicTask.getTaskName());
        epics.get(id).setDescription(epicTask.getDescription());
        updateEpicStatus(epicTask);
    }

    //распечатываем историю запросов
    @Override
    public void printHistory() {
        System.out.println("История последних 10 просмотров:");
        historyManager.getHistory().forEach(System.out::println);
    }

    //обновление статуса
    private void updateEpicStatus(EpicTask epicTask) {
        boolean done = subTasks.values().stream().filter(x -> x.getEpicId() == epicTask.getId())
                .anyMatch(x -> x.getStatus().equals(StatusType.DONE));
        boolean inProgress = subTasks.values().stream().filter(x -> x.getEpicId() == epicTask.getId())
                .anyMatch(x -> x.getStatus().equals(StatusType.IN_PROGRESS));

        if (epicTask.getSubtasks().size() == 0 | !done & !inProgress) {
            epicTask.setStatus(StatusType.NEW);
        } else if (epicTask.getSubtasks().size() == subTasks.values().stream()
                .filter(x -> x.getEpicId() == epicTask.getId())
                .filter(subtask -> subtask.getStatus().equals(StatusType.DONE)).count()) {
            epicTask.setStatus(StatusType.DONE);
        } else {
            epicTask.setStatus(StatusType.IN_PROGRESS);
        }
    }

    //ловим ошибки
    private void exceptionHandler(int id) {
        if (!tasks.containsKey(id) & !subTasks.containsKey(id) & !epics.containsKey(id)) {
            throw new TaskNotFined("Задача с идентификатором " + id + " не найдена.");
        }
    }
}
