package main;

import main.interfaces.TaskManager;
import main.models.EpicTask;
import main.models.Subtask;
import main.services.Managers;
import main.util.StatusType;

import java.time.Instant;

public class Main {

    static TaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {

        EpicTask epicTask = new EpicTask("Переезд", "новая квартира");
        inMemoryTaskManager.newEpicTask(epicTask);

        Subtask subtask1 = new Subtask("Вещи", "сложить все в коробки",
                5, Instant.ofEpochSecond(1717285397L), epicTask.getId());
        Subtask subtask2 = new Subtask("Грузчики", "найти помощников",
                4, Instant.ofEpochSecond(1726185697L), epicTask.getId());


        EpicTask epicTask2 = new EpicTask("Ужин", "подумать что приготовить на вечер");
        inMemoryTaskManager.newEpicTask(epicTask2);
        Subtask subtask3 = new Subtask("Магазин", "купить продукты для ужина",
                44, Instant.ofEpochSecond(1606185697L), epicTask2.getId());

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

        inMemoryTaskManager.changeSubtask(3, subtask1, StatusType.DONE);
        System.out.println(inMemoryTaskManager.getAllSubtask());

    }
}
