package tasks;

import enumeration.StatusOfTask;
import enumeration.TypeOfTask;
import manager.DTF;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, StatusOfTask.NEW, null, null);
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, StatusOfTask.NEW, startTime, duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
                ", startTime=" + startTimeToString(getStartTime()) +
                ", duration=" + durationToString(getDuration()) +
                '}';
    }

    public String epicToString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,\n",
                getId(),
                TypeOfTask.EPIC,
                getName(),
                getStatus(),
                getDescription(),
                startTimeToString(getStartTime()),
                durationToString(getDuration())
        );
    }
}

