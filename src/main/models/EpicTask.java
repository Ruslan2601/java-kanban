package main.models;

import main.util.TaskType;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subtasks = new ArrayList<>();
    private TaskType type = TaskType.EPICTASK;

    public EpicTask(String taskName, String description) {
        super(taskName, description);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Integer> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + super.getId() +
                ", taskName='" + super.getTaskName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() + '}' +
                "SubtasksList{" +
                "subtasks=" + subtasks +
                '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s",
                super.getId(), type, super.getTaskName(), super.getStatus(), super.getDescription());
    }
}
