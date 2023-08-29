package main.services;

import main.interfaces.HistoryManager;
import main.interfaces.TaskManager;
import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusType;
import main.util.TaskIntersectionsError;
import main.util.TaskNotFined;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private static int generateId = 0;

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subTasks = new HashMap<>();
    protected HashMap<Integer, EpicTask> epics = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>();

    protected HistoryManager historyManager = Managers.getDefaultHistory();

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
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllSubtask() {
        List<Integer> ids = new ArrayList(subTasks.keySet());
        for (Integer id : ids) {
            prioritizedTasks.remove(subTasks.get(id));
            subTasks.remove(id);
            historyManager.remove(id);
        }
        epics.values().forEach(x -> x.getSubtasks().clear());
        epics.values().forEach(this::updateEpicStatus);
        epics.values().forEach(this::updateEpicTime);
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
        addPrioritizedTasks(task);
        return task;
    }

    @Override
    public Subtask newSubtask(Subtask subtask) {
        final int id = ++generateId;
        subtask.setId(id);
        subTasks.put(id, subtask);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask.getId());
        updateEpicStatus(epics.get(subtask.getEpicId()));
        updateEpicTime(epics.get(subtask.getEpicId()));
        addPrioritizedTasks(subtask);
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
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        try {
            epics.get(subTasks.get(id).getEpicId()).getSubtasks()
                    .removeIf(task -> task == id);
            updateEpicStatus(epics.get(subTasks.get(id)
                    .getEpicId()));
            updateEpicTime(epics.get(subTasks.get(id)
                    .getEpicId()));
            prioritizedTasks.remove(subTasks.get(id));
            subTasks.remove(id);
        } catch (NullPointerException e) {
            throw new TaskNotFined("Задача с идентификатором " + id + " не найдена.");
        }
        historyManager.remove(id);
        prioritizedTasks.remove(subTasks.get(id));
    }

    @Override
    public void removeEpicTaskById(int id) {
        exceptionHandler(id);
        List<Integer> ids = new ArrayList(epics.get(id).getSubtasks());
        for (Integer idSubtask : ids) {
            historyManager.remove(idSubtask);
            subTasks.remove(idSubtask);
            prioritizedTasks.removeIf(task -> task.getId() == idSubtask);
        }
        epics.remove(id);
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
            tasks.get(id).setDescription(task.getDescription());
            tasks.get(id).setTaskName(task.getTaskName());
            tasks.get(id).setStartTime(task.getStartTime());
            tasks.get(id).setDuration(task.getDuration());
            tasks.get(id).setStatus(statusType);
            addPrioritizedTasks(tasks.get(id));
        }
    }

    @Override
    public void changeSubtask(int id, Subtask subtask, StatusType statusType) {
        exceptionHandler(id);
        subTasks.get(id).setEpicId(subtask.getEpicId());
        subTasks.get(id).setTaskName(subtask.getTaskName());
        subTasks.get(id).setDuration(subtask.getDuration());
        subTasks.get(id).setStartTime(subtask.getStartTime());
        subTasks.get(id).setDescription(subtask.getDescription());
        subTasks.get(id).setStatus(statusType);
        updateEpicStatus(epics.get(subtask.getEpicId()));
        updateEpicTime(epics.get(subtask.getEpicId()));
        addPrioritizedTasks(subTasks.get(id));
    }

    @Override
    public void changeEpicTask(int id, EpicTask epicTask) {
        exceptionHandler(id);
        epics.get(id).setTaskName(epicTask.getTaskName());
        epics.get(id).setDescription(epicTask.getDescription());
        updateEpicStatus(epics.get(id));
        updateEpicTime(epics.get(id));
    }

    //распечатываем историю запросов
    @Override
    public void printHistory() {
        System.out.println("История последних 10 просмотров:");
        historyManager.getHistory().forEach(System.out::println);
    }

    public static void setGenerateId(int generateId) {
        InMemoryTaskManager.generateId = generateId;
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

    //обновление времени
    private void updateEpicTime(EpicTask epicTask) {
        long duration;
        Instant end;
        Instant start;

        duration = subTasks.values().stream().filter(x -> x.getEpicId() == epicTask.getId()).mapToLong(Subtask::getDuration).sum();
        end = Objects.requireNonNull(subTasks.values().stream().filter(x -> x.getEpicId() == epicTask.getId())
                .max(Comparator.comparing(Subtask::getEndTime)).orElse(null)).getEndTime();
        start = Objects.requireNonNull(subTasks.values().stream().filter(x -> x.getEpicId() == epicTask.getId())
                .min(Comparator.comparing(Subtask::getStartTime)).orElse(null)).getStartTime();
        epicTask.setDuration(duration);
        epicTask.setStartTime(start);
        epicTask.setEndTime(end);
    }

    //список задач отсортированный по приоритету
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    //добавить задачу в список приоритета
    public void addPrioritizedTasks(Task task) {
        if (!validation(task)) {
            prioritizedTasks.add(task);
        } else {
            throw new TaskIntersectionsError("Невозможно добавить задачу " + task.getId() + ", найдено пересечение времени.");
        }
    }

    //проверка пересечения дат в списке приоритета
    private boolean validation(Task task) {
        boolean isIntersection = false;
        if (task.getStartTime() == null) {
            return isIntersection;
        }
        Instant startOfTask = task.getStartTime();
        Instant endOfTask = task.getEndTime();
        for (Task taskValue : prioritizedTasks) {
            if (taskValue.getStartTime() == null) {
                continue;
            }
            Instant startTime = taskValue.getStartTime();
            Instant endTime = taskValue.getEndTime();
            boolean isCovering = startTime.isBefore(startOfTask) && endTime.isAfter(endOfTask);
            boolean isOverlappingByEnd = startTime.isBefore(startOfTask) && endTime.isAfter(startOfTask);
            boolean isOverlappingByStart = startTime.isBefore(endOfTask) && endTime.isAfter(endOfTask);
            boolean isWithin = startTime.isAfter(startOfTask) && endTime.isBefore(endOfTask);
            isIntersection = isCovering || isOverlappingByEnd || isOverlappingByStart || isWithin;
        }
        return isIntersection;
    }

    //ловим ошибки
    private void exceptionHandler(int id) {
        if (!tasks.containsKey(id) & !subTasks.containsKey(id) & !epics.containsKey(id)) {
            throw new TaskNotFined("Задача с идентификатором " + id + " не найдена.");
        }
    }
}
