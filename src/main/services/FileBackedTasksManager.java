package main.services;

import main.interfaces.HistoryManager;
import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.util.ManagerSaveException;
import main.util.StatusType;
import main.util.TaskNotFined;
import main.util.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager(String path) {
        this.file = new File(path);
    }

    public FileBackedTasksManager() {
    }

    //удаляем все задачи
    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void removeAllEpicTask() {
        super.removeAllEpicTask();
        save();
    }

    //создаем новую задачу
    @Override
    public Task newTask(Task task) {
        Task task1 = super.newTask(task);
        save();
        return task1;
    }

    @Override
    public Subtask newSubtask(Subtask subtask) {
        Subtask subtask1 = super.newSubtask(subtask);
        save();
        return subtask1;
    }

    @Override
    public EpicTask newEpicTask(EpicTask epicTask) {
        EpicTask epicTask1 = super.newEpicTask(epicTask);
        save();
        return epicTask1;
    }

    //удаляем задачу по идентификатору
    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicTaskById(int id) {
        super.removeEpicTaskById(id);
        save();
    }

    //находим задачу по идентификатору
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask1 = super.getSubtaskById(id);
        save();
        return subtask1;
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epicTask1 = super.getEpicById(id);
        save();
        return epicTask1;
    }

    @Override
    public Task getTaskById(int id) {
        Task task1 = super.getTaskById(id);
        save();
        return task1;
    }

    //обновляем задачу
    @Override
    public void changeTask(int id, Task task, StatusType statusType) {
        super.changeTask(id, task, statusType);
        save();
    }

    @Override
    public void changeSubtask(int id, Subtask subtask, StatusType statusType) {
        super.changeSubtask(id, subtask, statusType);
        save();
    }

    @Override
    public void changeEpicTask(int id, EpicTask epicTask) {
        super.changeEpicTask(id, epicTask);
        save();
    }

    //сохраняем в файл все задачи и историю
    private void save() {
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,epic" + "\n");
            for (Task task : super.getAllTasks()) {
                fileWriter.write(task.toStringFromFile() + "\n");
            }
            for (EpicTask epicTask : super.getAllEpicTask()) {
                fileWriter.write(epicTask.toStringFromFile() + "\n");
            }
            for (Subtask subtask : super.getAllSubtask()) {
                fileWriter.write(subtask.toStringFromFile() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(super.historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла");
        }
    }


    //восстанавливаем задачи из файла
    public Task fromString(String value) {
        String[] strings = value.split(",");
        Instant instant = (!strings[5].equals("null")) ? Instant.parse(strings[5]) : null;
        switch (strings[1]) {
            case ("TASK"):
                Task task = new Task(strings[2], strings[4]);
                task.setId(Integer.parseInt(strings[0]));
                task.setStatus(StatusType.valueOf(strings[3]));
                task.setType(TaskType.valueOf(strings[1]));
                task.setDuration(Integer.parseInt(strings[6]));
                task.setStartTime(instant);
                return task;
            case ("SUBTASK"):
                Subtask subtask = new Subtask(strings[2], strings[4], Integer.parseInt(strings[7]));
                subtask.setId(Integer.parseInt(strings[0]));
                subtask.setStatus(StatusType.valueOf(strings[3]));
                subtask.setType(TaskType.valueOf(strings[1]));
                subtask.setDuration(Integer.parseInt(strings[6]));
                subtask.setStartTime(instant);
                return subtask;
            case ("EPICTASK"):
                EpicTask epicTask = new EpicTask(strings[2], strings[4], Integer.parseInt(strings[6]), instant);
                epicTask.setId(Integer.parseInt(strings[0]));
                epicTask.setStatus(StatusType.valueOf(strings[3]));
                epicTask.setType(TaskType.valueOf(strings[1]));
                return epicTask;
            default:
                throw new TaskNotFined("Задачи с таким типом не предусмотрены");
        }
    }

    //сохраняем всю историю в одну строку
    public static String historyToString(HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        manager.getHistory().forEach(x -> stringBuilder.append(x.getId()).append(","));
        return stringBuilder.toString();
    }

    //создаем из строки с историе список с id
    public static List<Integer> historyFromString(String value) {
        List<Integer> taskList = new ArrayList<>();
        if (!value.isEmpty()) {
            String[] strings = value.split(",");
            for (String str : strings) {
                taskList.add(Integer.parseInt(str));
            }
        }
        return taskList;
    }

    //загружаем в память и историю всю информацию из файла
    public static FileBackedTasksManager loadFromFile(File file) {
        List<String> strings = new ArrayList<>();
        FileBackedTasksManager manager = new FileBackedTasksManager();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                strings.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }

        if (strings.isEmpty()) {
            throw new ManagerSaveException("Файл пуст");
        }

        List<Integer> historyList = historyFromString(strings.get(strings.size() - 1));
        strings.remove(strings.size() - 1);
        strings.remove(0);

        int generateId = 0;

        for (String str : strings) {
            if (!str.isEmpty() || !str.isBlank()) {
                Task task = manager.fromString(str);

                if (task.getId() > generateId) {
                    generateId = task.getId();
                }

                if (task.getType().equals(TaskType.EPICTASK)) {
                    manager.epics.put(task.getId(), (EpicTask) task);
                } else if (task.getType().equals(TaskType.SUBTASK)) {
                    manager.subTasks.put(task.getId(), (Subtask) task);
                    manager.addPrioritizedTasks(task);
                } else {
                    manager.tasks.put(task.getId(), task);
                    manager.addPrioritizedTasks(task);
                }
            }
        }

        FileBackedTasksManager.setGenerateId(generateId);

        for (int id : historyList) {
            if (manager.tasks.containsKey(id)) {
                manager.historyManager.addHistory(manager.tasks.get(id));
            } else if (manager.subTasks.containsKey(id)) {
                manager.historyManager.addHistory(manager.subTasks.get(id));
            } else {
                manager.historyManager.addHistory(manager.epics.get(id));
            }
        }

        return manager;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("src/resources/history.csv");
        EpicTask epicTask = new EpicTask("Переезд", "новая квартира");
        fileBackedTasksManager.newEpicTask(epicTask);

        Subtask subtask1 = new Subtask("Вещи", "сложить все в коробки",
                5, Instant.ofEpochSecond(1717285397L), epicTask.getId());
        Subtask subtask2 = new Subtask("Грузчики", "найти помощников",
                4, Instant.ofEpochSecond(1726185697L), epicTask.getId());


        EpicTask epicTask2 = new EpicTask("Ужин", "подумать что приготовить на вечер");
        fileBackedTasksManager.newEpicTask(epicTask2);
        Subtask subtask3 = new Subtask("Магазин", "купить продукты для ужина",
                44, Instant.ofEpochSecond(1606185697L), epicTask2.getId());


        fileBackedTasksManager.newSubtask(subtask1);
        fileBackedTasksManager.newSubtask(subtask2);
        fileBackedTasksManager.newSubtask(subtask3);

        fileBackedTasksManager.newTask(new Task("task1", "desTask1",
                22, null));


        System.out.println(fileBackedTasksManager.getAllEpicTask());
        System.out.println(fileBackedTasksManager.getAllSubtask());
        System.out.println(fileBackedTasksManager.getAllTasks());

        fileBackedTasksManager.getSubtaskById(3);
        fileBackedTasksManager.getSubtaskById(4);
        fileBackedTasksManager.getEpicById(1);
        fileBackedTasksManager.getEpicById(2);
        fileBackedTasksManager.getSubtaskById(5);
        fileBackedTasksManager.getSubtaskById(3);
        fileBackedTasksManager.getSubtaskById(4);
        fileBackedTasksManager.getEpicById(1);
        fileBackedTasksManager.getEpicById(2);
        fileBackedTasksManager.getSubtaskById(5);

        fileBackedTasksManager.printHistory();
        System.out.println();
        System.out.println();


        FileBackedTasksManager fileBackedTasksManager2 = loadFromFile(new File("src/resources/history.csv"));
        fileBackedTasksManager2.printHistory();
        System.out.println();
        System.out.println(fileBackedTasksManager2.getAllEpicTask());
        System.out.println(fileBackedTasksManager2.getAllSubtask());
        System.out.println(fileBackedTasksManager.getAllTasks());
        fileBackedTasksManager2.getPrioritizedTasks();

    }

}
