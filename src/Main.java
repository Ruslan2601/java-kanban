import manager.TaskManager;
import models.EpicTask;
import models.StatusType;
import models.Subtask;

public class Main {

    static TaskManager taskManager;

    //создаем мапы для хранения всех данных
    static {
        taskManager = new TaskManager();
    }

    public static void main(String[] args) {

        EpicTask epicTask = new EpicTask("Переезд", "новая квартира");
        taskManager.newEpicTask(epicTask);
        Subtask subtask1 = new Subtask("Вещи", "сложить все в коробки", epicTask.getId());
        Subtask subtask2 = new Subtask("Грузчики", "найти помощников", epicTask.getId());

        EpicTask epicTask2 = new EpicTask("Ужин", "подумать что приготовить на вечер");
        taskManager.newEpicTask(epicTask2);
        Subtask subtask3 = new Subtask("Магазин", "купить продукты для ужина", epicTask2.getId());

        taskManager.newSubtask(subtask1);
        taskManager.newSubtask(subtask2);
        taskManager.newSubtask(subtask3);

        System.out.println(taskManager.getAllEpicTask());
        System.out.println(taskManager.getAllSubtask());

        taskManager.changeSubtask(3,
                new Subtask("Вещи", "сложить все в коробки", 1), StatusType.DONE);
        taskManager.changeSubtask(4,
                new Subtask("Грузчики", "найти помощников", 1), StatusType.DONE);
        taskManager.changeSubtask(5,
                new Subtask("Магазин", "купить продукты для ужина", 2), StatusType.IN_PROGRESS);

        System.out.println();
        System.out.println(taskManager.getAllEpicTask());
        System.out.println(taskManager.getAllSubtask());

        taskManager.removeEpicTaskById(2);
        taskManager.removeSubtaskById(3);
        taskManager.removeSubtaskById(4);

        System.out.println();
        System.out.println(taskManager.getAllEpicTask());
        System.out.println(taskManager.getAllSubtask());
    }
}
