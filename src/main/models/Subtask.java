package main.models;

import main.util.StatusType;
import main.util.TaskType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;
    private TaskType type = TaskType.SUBTASK;

    public Subtask(String taskName, String description, int epicId) {
        super(taskName, description);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String description, long duration, Instant startTime,  int epicId) {
        super(taskName, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String description, long duration, Instant startTime, StatusType status, int epicId) {
        super(taskName, description, duration, startTime, status);
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
                ", start time=" + super.getStartTime().toEpochMilli() + '\'' +
                ", duration=" + super.getDuration() + ", end time=" + getEndTime().toEpochMilli() + '\'' +
                ", status=" + super.getStatus() + '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                getId(), type, getTaskName(), getStatus(),
                getDescription(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(getStartTime().atZone(ZoneId.systemDefault())),
                getDuration(), getEpicId());
    }
}
