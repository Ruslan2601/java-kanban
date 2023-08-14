import interfaces.TaskManager;
import models.EpicTask;
import services.Managers;
import models.Subtask;

public class Main {

    static TaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {

        EpicTask epicTask = new EpicTask("Переезд", "новая квартира");
        inMemoryTaskManager.newEpicTask(epicTask);
        Subtask subtask1 = new Subtask("Вещи", "сложить все в коробки", epicTask.getId());
        Subtask subtask2 = new Subtask("Грузчики", "найти помощников", epicTask.getId());

        EpicTask epicTask2 = new EpicTask("Ужин", "подумать что приготовить на вечер");
        inMemoryTaskManager.newEpicTask(epicTask2);
        Subtask subtask3 = new Subtask("Магазин", "купить продукты для ужина", epicTask2.getId());

        inMemoryTaskManager.newSubtask(subtask1);
        inMemoryTaskManager.newSubtask(subtask2);
        inMemoryTaskManager.newSubtask(subtask3);

        System.out.println(inMemoryTaskManager.getAllEpicTask());
        System.out.println(inMemoryTaskManager.getAllSubtask());
        inMemoryTaskManager.getSubtaskById(3);
        inMemoryTaskManager.getSubtaskById(4);
        inMemoryTaskManager.getEpicById(1);
        inMemoryTaskManager.getEpicById(2);
        inMemoryTaskManager.getSubtaskById(5);
        inMemoryTaskManager.getSubtaskById(3);
        inMemoryTaskManager.getSubtaskById(4);
        inMemoryTaskManager.getEpicById(1);
        inMemoryTaskManager.getEpicById(2);
        inMemoryTaskManager.getSubtaskById(5);

        inMemoryTaskManager.printHistory();
        System.out.println();
        inMemoryTaskManager.removeEpicTaskById(2);
        inMemoryTaskManager.printHistory();
        System.out.println();

    }
}
