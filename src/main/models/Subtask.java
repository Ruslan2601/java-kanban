package main.models;

import main.util.TaskType;

public class Subtask extends Task {
    private int epicId;
    private TaskType type = TaskType.SUBTASK;

    public Subtask(String taskName, String description, int epicId) {
        super(taskName, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", taskName='" + super.getTaskName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() + '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s",
                super.getId(), type, super.getTaskName(), super.getStatus(), super.getDescription(), getEpicId());
    }
}
