package Test;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.service.Managers;
import http.HttpTaskManager;
import http.KVServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {

    @Test
    void saveAndLoad() throws IOException, InterruptedException {

        KVServer server;
        server = new KVServer();
        server.start();
        HttpTaskManager taskManager = Managers.getDefault(8077);

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
        SubTask subTask3 = new SubTask("Подзадача №3", "Описание Подзадачи №3", 3);

        taskManager.saveSubTask(subTask1); //Создание подзадачи и присвоение ее конкретному Епику
        taskManager.saveSubTask(subTask2); //Создание подзадачи и присвоение ее конкретному Епику
        taskManager.saveSubTask(subTask3); //Создание подзадачи и присвоение ее конкретному Епику

        taskManager.getTask(1);//Получение задачи должно отразится в истории
        taskManager.getTask(2);//Получение задачи должно отразится в истории
        taskManager.getTask(1);//Получение задачи должно отразится в истории
        taskManager.getTask(2);//Получение задачи должно отразится в истории

        taskManager.getEpic(3);//Получение Епика должно отразится в истории
        taskManager.getEpic(4);//Получение Епика должно отразится в истории
        taskManager.getEpic(3);//Получение Епика должно отразится в истории
        taskManager.getEpic(4);//Получение Епика должно отразится в истории

        taskManager.getSubTask(5);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(6);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(7);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(5);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(6);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(7);//Получение Подзадачи должно отразится в истории

        taskManager.clean();

        List<Task> tasks = taskManager.getAllTasks();
        List<Epic> epics = taskManager.getAllEpic();
        List<SubTask> subTasks = taskManager.getAllSubTask();


        assertEquals(0,tasks.size(), "Список Таск не пуст");
        assertEquals(0,epics.size(), "Список Епик не пуст");
        assertEquals(0,subTasks.size(), "Список Сабтаск не пуст");

        taskManager.load();

        List<Task> tasksLoad = taskManager.getAllTasks();
        List<Epic> epicsLoad = taskManager.getAllEpic();
        List<SubTask> subTasksLoad = taskManager.getAllSubTask();

        assertNotNull(tasksLoad, "Список Таск не пуст");
        assertNotNull(epicsLoad, "Список Епик не пуст");
        assertNotNull(subTasksLoad, "Список Сабтаск не пуст");

    }

}
