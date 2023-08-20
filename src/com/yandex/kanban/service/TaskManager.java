package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;

import java.util.List;

public interface TaskManager {

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
    public void removeEpik(int id);

    //Удаление всех задач TASK
    public void removeAllTask ();

    //Удаление всех подзадач SUBTASK
    public void removeAllSubTask();

    //Удаление всех епик и подзадачи тоже EPIK
    public void removeAllEpik ();

    //Получение задачи по id TASK
    public Task getTask(int id);

    //Получение подзадачи по id SUBTASK
    public SubTask getSubTask(int epicId);

    //Получение епика по id EPIK
    public Epic getEpic(int epicId);

    //Получение всех задач TASK
    public List<Task> getAllTasks();

    //Получение всех епиков EPIK
    public List<Task> getAllEpik();

    //Получение всех подзадачь из всех эпиков без самих эпиков SUBTASK
    public List<Task> getAllSubTask();

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