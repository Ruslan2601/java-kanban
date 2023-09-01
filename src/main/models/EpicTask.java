package main.models;

import main.util.StatusType;
import main.util.TaskType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {

    private List<Integer> subtasks = new ArrayList<>();
    private Instant endTime;

    public EpicTask(String taskName, String description) {
        super(taskName, description);
        super.type = TaskType.EPICTASK;
    }

    public EpicTask(String taskName, String description, long duration, Instant startTime) {
        super(taskName, description, duration, startTime);
        this.endTime = super.getEndTime();
        super.type = TaskType.EPICTASK;
    }

    public EpicTask(String taskName, String description, long duration, Instant startTime, StatusType status, List<Integer> subtasks) {
        super(taskName, description, duration, startTime, status);
        this.endTime = super.getEndTime();
        this.subtasks = subtasks;
        super.type = TaskType.EPICTASK;
    }

    public EpicTask(int id, String taskName, String description, StatusType status) {
        super(id, taskName, description, status);
        this.endTime = super.getEndTime();
        super.type = TaskType.EPICTASK;
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
                ", status='" + getStatus() + ", start time=" + getStartTime() +
                ", duration=" + getDuration() + ", end time=" + getEndTime() + '\'' +
                "SubtasksList{" +
                "subtasks=" + subtasks +
                '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                getId(), getType(), getTaskName(), getStatus(), getDescription(),
                getStartTime(),
                getDuration());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EpicTask epicTask = (EpicTask) o;
        return Objects.equals(subtasks, epicTask.subtasks) && type == epicTask.type && Objects.equals(endTime, epicTask.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks, type, endTime);
    }
}
