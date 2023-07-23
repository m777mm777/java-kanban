import java.util.ArrayList;
import java.util.List;

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

        taskManager.updateTask(task1,TaskStatus.IN_PROGRESS); //Смена статуса задачи 1 должно отразится в истории
        taskManager.updateSubTask(subTask3,TaskStatus.DONE); //Смена статуса подзадачи 1 должно отразится в истории


        List<Task> history = taskManager.getHistory(); //Получение Истории просмотров

        System.out.println("История просмотров задач " + history);//Печать истории


    }
}