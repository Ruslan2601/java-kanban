package services;

import interfaces.HistoryManager;
import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    //добавляем задачу в историю просмотров
    public void addHistory(Task task) {
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }

    //получаем историю просмотров
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
