package tasks;

import enumeration.StatusOfTask;
import enumeration.TypeOfTask;
import manager.DTF;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(0));
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, StatusOfTask.NEW, startTime, duration);
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
                ", startTime=" + getStartTime().format(DTF.getDTF()) +
                ", endTime=" + getEndTime().format(DTF.getDTF()) +
                ", duration=" + getDuration().toMinutes() +
                '}';
    }

    public String epicToString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,\n",
                getId(),
                TypeOfTask.EPIC,
                getName(),
                getStatus(),
                getDescription(),
                getStartTime().format(DTF.getDTF()),
                getDuration().toMinutes()
        );
    }
}

