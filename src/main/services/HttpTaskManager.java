package main.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import main.models.EpicTask;
import main.models.Subtask;
import main.models.Task;
import main.servers.KVTaskClient;

import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    private Gson gson = Managers.getGson();

    public HttpTaskManager(String url) {
        this.client = new KVTaskClient(url);
    }

    @Override
    public void save() {
        client.put("tasks", gson.toJson(tasks.values()));
        client.put("subtasks", gson.toJson(subTasks.values()));
        client.put("epics", gson.toJson(epics.values()));

        List<Integer> historyIds = historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        client.put("history", gson.toJson(historyIds));
    }

    public void load() {
        loadTasks("tasks");
        loadTasks("epics");
        loadTasks("subtasks");
        loadHistory();
    }

    private void loadTasks(String type) {
        JsonElement jsonElement = JsonParser.parseString(client.load(type));
        JsonArray jsonHistoryArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonHistoryArray) {
            switch (type) {
                case "tasks":
                    Task task = gson.fromJson(element.getAsJsonObject(), Task.class);
                    tasks.put(task.getId(), task);
                    addPrioritizedTasks(task);
                    break;
                case "subtasks":
                    Subtask subtask = gson.fromJson(element.getAsJsonObject(), Subtask.class);
                    subTasks.put(subtask.getId(), subtask);
                    addPrioritizedTasks(subtask);
                    break;
                case "epics":
                    EpicTask epicTask = gson.fromJson(element.getAsJsonObject(), EpicTask.class);
                    epics.put(epicTask.getId(), epicTask);
                    break;
                default:
                    System.out.println("Такого типа задачи нет");
                    return;
            }
        }
    }

    private void loadHistory() {
        JsonElement jsonElement = JsonParser.parseString(client.load("history"));
        JsonArray jsonHistoryArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonHistoryArray) {
            int id = element.getAsInt();
            if (tasks.containsKey(id)) {
                historyManager.addHistory(tasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.addHistory(epics.get(id));
            } else if (subTasks.containsKey(id)) {
                historyManager.addHistory(subTasks.get(id));
            }
        }
    }
}
