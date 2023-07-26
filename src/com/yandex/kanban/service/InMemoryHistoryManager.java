package com.yandex.kanban.service;
import com.yandex.kanban.model.Task;


import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    //Коллекция храненя истории
    private final List<Task> history = new LinkedList<>();
    //Максимальный размер количество хранение истории
    private static final int HISTORY_SIZE = 10;

    @Override
   public List<Task> getHistory() {
        return List.copyOf(history);
    }

    //Добавление в историю просмотров и удаление старых
    @Override
   public void addHistory(Task task) {
        if (task == null) {
            return;
        }
        history.add(task);
        if (history.size() > HISTORY_SIZE) {
            history.remove(0);
        }
    }

}
