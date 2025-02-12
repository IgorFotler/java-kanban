package tasks;

import enumeration.StatusOfTask;
import enumeration.TypeOfTask;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, StatusOfTask.NEW);
    }

    public void addSubTaskId(Subtask subTask) {
        subtasksId.add(subTask.getId());
    }

    public void deleteSubtaskId(int id) {
        subtasksId.remove((Integer) id);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void deleteAllSubtasksId() {
        subtasksId.clear();
    }

    @Override
    public String toString() {
        return "epics.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    public String epicToString() {
        return String.format("%s,%s,%s,%s,%s,\n", getId(), TypeOfTask.EPIC, getName(), getStatus(), getDescription());
    }
}

