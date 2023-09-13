package com.yandex.kanban.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTaskIds = new ArrayList<>();

    private LocalDateTime endDateTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, TaskStatus status, int id, LocalDateTime time, Integer minutes) {
        super(name, description, status, id, time, minutes);
    }

    public Epic(String name, String description, int id, LocalDateTime time, Integer minutes) {
        super(name, description, id, time, minutes);
    }

    @Override
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public TaskType getType() { return TaskType.EPIC; }

    public List<Integer> getSubTaskId() {
        return subTaskIds;
    }

    public void addSubtaskIds(int id) {
        subTaskIds.add(id);
    }

    public void removeAllSubtaskIds() {
        subTaskIds.clear();
    }

    public void removeIdFromSubtaskIds(Integer id) {
        subTaskIds.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(id, epic.id)
                && Objects.equals(name, epic.name)
                && Objects.equals(description, epic.description)
                && Objects.equals(status, epic.status)
                && Objects.equals(subTaskIds, epic.subTaskIds)
                && Objects.equals(startDateTime, epic.startDateTime)
                && Objects.equals(duration, epic.duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subTaskIds='" + subTaskIds + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, subTaskIds, startDateTime, duration);
    }

}