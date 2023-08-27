package main.models;

import main.util.StatusType;
import main.util.TaskType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subtasks = new ArrayList<>();
    private TaskType type = TaskType.EPICTASK;
    private Instant endTime;

    public EpicTask(String taskName, String description) {
        super(taskName, description);
    }

    public EpicTask(String taskName, String description, long duration, Instant startTime) {
        super(taskName, description, duration, startTime);
        this.endTime = super.getEndTime();
    }

    public EpicTask(String taskName, String description, long duration, Instant startTime, StatusType status, List<Integer> subtasks) {
        super(taskName, description, duration, startTime, status);
        this.endTime = super.getEndTime();
        this.subtasks = subtasks;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
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
                "id=" + getId() +
                ", taskName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + ", start time=" + getStartTime().toEpochMilli() +
                ", duration=" + getDuration() + ", end time=" + getEndTime().toEpochMilli() + '\'' +
                "SubtasksList{" +
                "subtasks=" + subtasks +
                '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                getId(), type, getTaskName(), getStatus(), getDescription(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(getStartTime().atZone(ZoneId.systemDefault())),
                getDuration());
    }
}
