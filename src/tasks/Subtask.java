package tasks;

import enumeration.StatusOfTask;
import enumeration.TypeOfTask;
import manager.DTF;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, StatusOfTask status, LocalDateTime startTime, Duration duration, Integer epicId) {
        super(name, description, status, startTime, duration);
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
                ", startTime=" + startTimeToString(getStartTime()) +
                ", duration=" + durationToString(getDuration()) +
                '}';
    }

    public String subtaskToString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,\n",
                getId(),
                TypeOfTask.SUBTASK,
                getName(),
                getStatus(),
                getDescription(),
                startTimeToString(getStartTime()),
                durationToString(getDuration()),
                getEpicId());
    }
}
