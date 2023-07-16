import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

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

        System.out.println("Все задачи " + taskManager.getAllTasks());
        System.out.println("Все эпики  " + taskManager.getAllEpik());
        System.out.println("Все подзадачи  " + taskManager.getAllSubTask());

        taskManager.updateTask(task1,"IN_PROGRESS"); //Смена статуса задачи 1
        taskManager.updateSubTask(subTask3,"DONE"); //Смена статуса подзадачи 1
        taskManager.checkStatusEpik(); //Сверка и обновление статуса всех епиков

        System.out.println("Измененный статус задачи 1 " + taskManager.getByTaskId(task1.getId()));
        System.out.println("Измененный статус подзадачи 3 " + taskManager.getBySubTaskId(subTask3.getSubTaskId()));
        System.out.println("Измененный статус Епик2 " + taskManager.getByEpikId(epic2.getId()));

        taskManager.removeTask(1); // Удаление задачи с id 1
        taskManager.removeEpik(3); // Удаление Епика c id 3 и следовательно его подзадач
    }
}