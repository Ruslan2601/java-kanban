import manager.TaskManager;
import models.EpicTask;
import models.StatusType;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {

    static TaskManager taskManager;

    //создаем мапу для хранения всех данных
    static {
        HashMap<StatusType, List<Task>> listHashMap = new HashMap<>();
        listHashMap.put(StatusType.NEW, new ArrayList<>());
        listHashMap.put(StatusType.IN_PROGRESS, new ArrayList<>());
        listHashMap.put(StatusType.DONE, new ArrayList<>());
        taskManager = new TaskManager(listHashMap);
    }

    public static void main(String[] args) {

        Subtask subtask1 = new Subtask("Вещи", "сложить все в коробки");
        Subtask subtask2 = new Subtask("Грузчики", "найти помощников");
        EpicTask epicTask = new EpicTask("Переезд", "новая квартира");
        epicTask.setSubtasks(new ArrayList<>(Arrays.asList(subtask1, subtask2)));

        Subtask subtask3 = new Subtask("Магазин", "купить продукты для ужина");
        EpicTask epicTask2 = new EpicTask("Ужин", "подумать что приготовить на вечер");
        epicTask2.setSubtasks(new ArrayList<>(Arrays.asList(subtask3)));

        //создаем 2 эпика
        taskManager.newTask(epicTask);
        taskManager.newTask(epicTask2);

        //распечатываем
        System.out.println(taskManager.getAllTasks());

        //изменяем статус у подзадачи
        taskManager.changeTask(3, subtask3, StatusType.IN_PROGRESS);

        //распечатываем
        System.out.println(taskManager.getAllTasks());

        //удаляем подзадачу
        taskManager.removeTaskById(3);

        //распечатываем
        System.out.println(taskManager.getAllTasks());

        //удаляем весь эпик
        taskManager.removeTaskById(2);

        //распечатываем
        System.out.println(taskManager.getAllTasks());

    }
}
