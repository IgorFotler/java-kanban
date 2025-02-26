package tasks;

import enumeration.StatusOfTask;
import enumeration.TypeOfTask;
import manager.DTF;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    private Integer id;
    private String name;
    private String description;
    private StatusOfTask status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description, StatusOfTask status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTimeToString(getStartTime()) +
                ", duration=" + durationToString(getDuration()) +
                '}';
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusOfTask getStatus() {
        return status;
    }

    public void setStatus(StatusOfTask status) {
        this.status = status;
    }

    public String taskToString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,\n",
                getId(),
                TypeOfTask.TASK,
                getName(),
                getStatus(),
                getDescription(),
                startTimeToString(getStartTime()),
                durationToString(getDuration())
                );
    }

    public String startTimeToString(LocalDateTime startTime) {
        if (startTime != null) {
            return getStartTime().format(DTF.getDTF());
        } else {
            return "null";
        }
    }

    public String durationToString(Duration duration) {
        if (duration != null) {
            return Long.toString(getDuration().toMinutes());
        } else {
            return "null";
        }
    }
}
