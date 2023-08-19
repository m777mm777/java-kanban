package com.yandex.kanban.model;

public class SubTask extends Task {

    private final Integer epicId;

    public SubTask(String name, String description, TaskStatus status, int id, Integer epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
        this.tupe = TaskTupe.SUBTASK;
    }
    public SubTask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
        this.tupe = TaskTupe.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

}
