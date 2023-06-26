package models;

public class Subtask extends Task {
    private final EpicTask epicTask;

    public Subtask(String taskName, String description, EpicTask epicTask) {
        super(taskName, description);
        this.epicTask = epicTask;
    }

    public EpicTask getEpicTask() {
        return epicTask;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", taskName='" + super.getTaskName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() + '}';
    }
}
