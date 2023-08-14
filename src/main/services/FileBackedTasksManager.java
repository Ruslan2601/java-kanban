package main.services;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final String path;

    public FileBackedTasksManager(String path) {
        this.path = path;
    }

    private void save() {

    }
}
