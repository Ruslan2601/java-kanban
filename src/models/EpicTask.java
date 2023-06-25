package models;

import java.util.List;

public class EpicTask extends Task {

    List<Subtask> subtasks;

    public EpicTask(String taskName, String description) {
        super(taskName, description);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + super.getId() +
                ", taskName='" + super.getTaskName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() + '}' +
                "EpicTask{" +
                "subtasks=" + subtasks +
                '}';
    }
}
