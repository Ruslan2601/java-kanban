package main.models;

import main.util.StatusType;
import main.util.TaskType;

import java.time.Instant;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, String description, int epicId) {
        super(taskName, description);
        this.epicId = epicId;
        super.type = TaskType.SUBTASK;
    }

    public Subtask(String taskName, String description, long duration, Instant startTime,  int epicId) {
        super(taskName, description, duration, startTime);
        this.epicId = epicId;
        super.type = TaskType.SUBTASK;
    }

    public Subtask(String taskName, String description, long duration, Instant startTime, StatusType status, int epicId) {
        super(taskName, description, duration, startTime, status);
        this.epicId = epicId;
        super.type = TaskType.SUBTASK;
    }

    public Subtask(int id, String taskName, String description, long duration, Instant startTime, StatusType status, int epicId) {
        super(id, taskName, description, duration, startTime, status);
        this.epicId = epicId;
        super.type = TaskType.SUBTASK;
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
                ", start time=" + super.getStartTime() + '\'' +
                ", duration=" + super.getDuration() + ", end time=" + getEndTime() + '\'' +
                ", status=" + super.getStatus() + '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                getId(), getType(), getTaskName(), getStatus(),
                getDescription(),
                getStartTime(),
                getDuration(),
                getEpicId());
    }
}
