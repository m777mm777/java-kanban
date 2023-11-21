package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    //Очистка
    public default void clean() {

    }
    //Возврат коллекции задач по приоритету
    public Set<Task> getPrioritizedTasks();
    // Создание задачи TASK
    public Task saveTask(Task task);

    //Создание епика EPIK
    public Epic saveEpic(Epic epic);

    //Создание подзадачи в определенный епик с проверкой наличия епика SUBTASK
    public SubTask saveSubTask(SubTask subTask);

    //Удаление задачи по id TASK
    public void removeTask(int id);

    //Удаление подзадачи по id SUBTASK

    public void removeSubTask(Integer id);

    //Удаление эпика по id и следовательно всех его подзадач EPIK
    public void removeEpic(int id);

    //Удаление всех задач TASK
    public void removeAllTask();

    //Удаление всех подзадач SUBTASK
    public void removeAllSubTask();

    //Удаление всех епик и подзадачи тоже EPIK
    public void removeAllEpic();

    //Получение задачи по id TASK
    public Task getTask(int id);

    //Проверка есть ли задачи по id TASK
    public boolean chekTask(int id);

    //Получение подзадачи по id SUBTASK
    public SubTask getSubTask(int epicId);

    //Проверка есть ли задачи по id SubTask
    public boolean chekSubTask(int id);

    //Получение епика по id EPIK
    public Epic getEpic(int epicId);

    //Проверка есть ли задачи по id Epic
    public boolean chekEpic(int id);

    //Получение всех задач TASK
    public List<Task> getAllTasks();

    //Получение всех епиков EPIK
    public List<Epic> getAllEpic();

    //Получение всех подзадачь из всех эпиков без самих эпиков SUBTASK
    public List<SubTask> getAllSubTask();

    //Получение всех задач одного Эпика
    public List<SubTask> getSubtaskByEpik(int id);

    //Обновление-Перезапись задач с сохранением id
    public void updateTask(Task task);

    //Обновление-Перезапись Епика с сохранением id
    public void updateEpic(Epic epic);

    //Обновление-Перезапись подзадачи с сохранением id для сверщика Епиков
    public void updateSubTask(SubTask subTask);

    public List<Task> getHistory();

}