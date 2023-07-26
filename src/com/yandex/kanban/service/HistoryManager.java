package com.yandex.kanban.service;
import com.yandex.kanban.model.Task;
import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();

    void addHistory(Task task);
}
