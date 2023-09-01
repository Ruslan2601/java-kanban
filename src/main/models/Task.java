package main.models;

import main.util.StatusType;
import main.util.TaskType;

import java.time.Instant;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private int id;
    private String taskName;
    private String description;
    private long duration;
    private Instant startTime;
    private StatusType status = StatusType.NEW;
    protected TaskType type;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
        this.type = TaskType.TASK;
    }

    public Task(String taskName, String description, long duration, Instant startTime, StatusType status) {
        this.taskName = taskName;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public Task(int id, String taskName, String description, long duration, Instant startTime, StatusType status) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public Task(String taskName, String description, long duration, Instant startTime) {
        this.taskName = taskName;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.type = TaskType.TASK;
    }

    public Task(int id, String taskName, String description, StatusType status) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
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
        if (startTime != null) {
            return startTime.plusSeconds(duration * seconds);
        }
        return null;
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
                ", start time=" + startTime +
                ", duration=" + duration + ", end time=" + getEndTime() +
                ", status=" + status + '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                id, type, taskName, status, description,
                getStartTime(),
                duration);
    }

    @Override
    public int compareTo(Task task) {
        if (task.getStartTime() == null) {
            return -1;
        } else if (this.startTime == null) {
            return 1;
        }
        return this.startTime.compareTo(task.startTime);
    }
}
