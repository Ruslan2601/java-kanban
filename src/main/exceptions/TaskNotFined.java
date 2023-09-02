package main.exceptions;

public class TaskNotFined extends RuntimeException{
    public TaskNotFined(String msg) {
        super(msg);
    }

}
