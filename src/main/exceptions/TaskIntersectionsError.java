package main.exceptions;

public class TaskIntersectionsError extends RuntimeException {
    public TaskIntersectionsError(String message) {
        super(message);
    }
}
