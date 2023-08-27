package main.models;

import main.util.StatusType;
import main.util.TaskType;

import java.time.Instant;
import java.util.Objects;

public class Task {
    private int id;
    private String taskName;
    private String description;
    private long duration;
    private Instant startTime;
    private StatusType status = StatusType.NEW;
    private TaskType type = TaskType.TASK;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
    }

    public Task(String taskName, String description, long duration, Instant startTime, StatusType status) {
        this.taskName = taskName;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = status;
    }

    public Task(String taskName, String description, long duration, Instant startTime) {
        this.taskName = taskName;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public Instant getEndTime() {
        long seconds = 60L;
        return startTime.plusSeconds(duration * seconds);
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(taskName, task.taskName) &&
                Objects.equals(description, task.description) && Objects.equals(startTime, task.startTime) &&
                status == task.status && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, description, duration, startTime, status, type);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", start time=" + startTime.toEpochMilli() + '\'' +
                ", duration=" + duration + ", end time=" + getEndTime().toEpochMilli() + '\'' +
                '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                id, type, taskName, status, description, startTime, duration);
    }
}
