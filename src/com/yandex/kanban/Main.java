package com.yandex.kanban;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import com.yandex.kanban.model.TaskStatus;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача №1", "Описание задачи №1");
        Task task2 = new Task("Задача №2", "Описание задачи №2");

        taskManager.saveTask(task1); //Создание задачи
        taskManager.saveTask(task2); //Создание задачи

        Epic epic1 = new Epic("Эпик №1", "Описание эпика №1");
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");

        taskManager.saveEpic(epic1); //Создание Епика
        taskManager.saveEpic(epic2); //Создание Епика

        SubTask subTask1 = new SubTask("Подзадача №1", "Описание Подзадачи №1", 3);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание Подзадачи №2", 3);
        SubTask subTask3 = new SubTask("Подзадача №3", "Описание Подзадачи №3", 4);

        taskManager.saveSubTask(subTask1); //Создание подзадачи и присвоение ее конкретному Епику
        taskManager.saveSubTask(subTask2); //Создание подзадачи и присвоение ее конкретному Епику
        taskManager.saveSubTask(subTask3); //Создание подзадачи и присвоение ее конкретному Епику

        taskManager.getByEpikId(3);//Получение Епика должно отразится в истории
        taskManager.getByEpikId(4);//Получение Епика должно отразится в истории

        task1 = new Task("Задача №1", "Описание задачи №1", TaskStatus.IN_PROGRESS, 1);
        taskManager.updateTask(task1); //Смена статуса задачи 1 должно отразится в истории

        subTask3 = new SubTask("Задача №1", "Описание задачи №1", TaskStatus.IN_PROGRESS,7, 4);
        taskManager.updateSubTask(subTask3); //Смена статуса подзадачи 1 должно отразится в истории

        epic1 = new Epic("Обнова епик 1", "Обнова епик 1", 3);
        taskManager.updateEpic(epic1); //Смена статуса подзадачи 1 должно отразится в истории

        System.out.println("История просмотров задач " + taskManager.getHistory());//Печать истории
        System.out.println("все епики " + taskManager.getAllEpik());//Печать истории
        System.out.println("все сабтаски " + taskManager.getAllSubTask());//Печать истории

    }
}