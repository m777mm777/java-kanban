package com.yandex.kanban.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private final Integer epicId;

    public SubTask(String name, String description,
                   TaskStatus status, int id, LocalDateTime time, Integer minutes, Integer epicId)
    {
        super(name, description, status, id, time, minutes);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() { return TaskType.SUBTASK; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(id, subTask.id)
                && Objects.equals(name, subTask.name)
                && Objects.equals(description, subTask.description)
                && Objects.equals(status, subTask.status)
                && Objects.equals(startDateTime, subTask.startDateTime)
                && Objects.equals(duration, subTask.duration)
                && Objects.equals(epicId, subTask.epicId);
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
                ", epicId='" + epicId + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startDateTime, duration, epicId);
    }
}

