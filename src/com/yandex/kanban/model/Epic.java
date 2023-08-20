package com.yandex.kanban.model;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, TaskStatus status, int id) {
        super(name, description, status, id);
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
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
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subTaskIds='" + subTaskIds + '\'' +
                '}';
    }

}