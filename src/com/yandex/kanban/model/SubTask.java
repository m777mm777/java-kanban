package com.yandex.kanban.model;
import com.yandex.kanban.service.TaskStatus;

public class SubTask extends Task {

    private Integer epicId;

    public SubTask(String name, String description, TaskStatus status, int id, Integer epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }
    public SubTask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getSubTaskId() {
        return id;
    }

    public int getEpicId() {
        return epicId;
    }

}
