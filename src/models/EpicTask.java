package models;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subtasks = new ArrayList<>();

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
}
