import manager.TaskManager;
import models.EpicTask;
import models.StatusType;
import models.Subtask;

import java.util.ArrayList;

public class Main {

    static TaskManager taskManager;

    //создаем мапы для хранения всех данных
    static {
        taskManager = new TaskManager(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public static void main(String[] args) {

        EpicTask epicTask = new EpicTask("Переезд", "новая квартира");
        Subtask subtask1 = new Subtask("Вещи", "сложить все в коробки", epicTask);
        Subtask subtask2 = new Subtask("Грузчики", "найти помощников", epicTask);

        EpicTask epicTask2 = new EpicTask("Ужин", "подумать что приготовить на вечер");
        Subtask subtask3 = new Subtask("Магазин", "купить продукты для ужина", epicTask2);

        taskManager.newEpicTask(epicTask);
        taskManager.newEpicTask(epicTask2);

        taskManager.newSubtask(subtask1);
        taskManager.newSubtask(subtask2);
        taskManager.newSubtask(subtask3);

        System.out.println(taskManager.getAllEpicTask());
        System.out.println(taskManager.getAllSubtask());

        taskManager.changeSubtask(1,
                new Subtask("Вещи", "сложить все в коробки", taskManager.getEpicById(0)), StatusType.DONE);
        taskManager.changeSubtask(2,
                new Subtask("Грузчики", "найти помощников", taskManager.getEpicById(0)), StatusType.DONE);
        taskManager.changeSubtask(4,
                new Subtask("Магазин", "купить продукты для ужина", taskManager.getEpicById(3)), StatusType.IN_PROGRESS);

        System.out.println();
        System.out.println(taskManager.getAllEpicTask());
        System.out.println(taskManager.getAllSubtask());

        taskManager.removeEpicTaskById(0);
        taskManager.removeSubtaskById(7);

        System.out.println();
        System.out.println(taskManager.getAllEpicTask());
        System.out.println(taskManager.getAllSubtask());
    }
}
