package tasks;

import enumeration.StatusOfTask;
import enumeration.TypeOfTask;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, StatusOfTask status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "subtasks.Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    public String subtaskToString() {
        return String.format("%s,%s,%s,%s,%s,%s,\n", getId(), TypeOfTask.SUBTASK, getName(), getStatus(), getDescription(), getEpicId());
    }
}
