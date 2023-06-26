package models;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Subtask> subtasks = new ArrayList<>();

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
        return "EpicTask{" +
                "id=" + super.getId() +
                ", taskName='" + super.getTaskName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() + '}' +
                "SubtasksList{" +
                "subtasks=" + subtasks +
                '}';
    }
}
