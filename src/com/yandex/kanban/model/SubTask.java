package com.yandex.kanban.model;

import java.util.Objects;

public class SubTask extends Task {
    private final Integer epicId;

    public SubTask(String name, String description, TaskStatus status, int id, Integer epicId) {
        super(name, description, status, id);
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
                && Objects.equals(epicId, subTask.epicId);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subTaskIds='" + epicId + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, epicId);
    }
}

