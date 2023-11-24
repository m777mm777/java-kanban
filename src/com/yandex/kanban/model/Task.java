package com.yandex.kanban.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected LocalDateTime startDateTime;
    protected int duration;
    private final String timeDatePattern = "dd.MM.yyyy HH:mm";

    public Task(String name, String description, TaskStatus status, int id, LocalDateTime time, int minutes) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startDateTime = time;
        this.duration = minutes;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        this.duration = 0;

    }

    public Task(String name, String description, int id, LocalDateTime time, int minutes) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.id = id;
        this.startDateTime = time;
        this.duration = minutes;
    }

    public TaskType getType() { return TaskType.TASK; }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        if (startDateTime == null) {
            return null;
        }
        return startDateTime.plusMinutes(duration);
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status)
                && Objects.equals(startDateTime, task.startDateTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startDateTime, duration);
    }

}