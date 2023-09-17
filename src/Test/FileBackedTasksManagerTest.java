package Test;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.service.file.FileBackedTasksManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.yandex.kanban.service.file.FileBackedTasksManager.loadFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTasksManagerTest extends InMemoryTaskManagerTest <FileBackedTasksManager> {

    //Проверка восстановления задач
    @Test
    public void chekLoadTaskStorage() {
        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);
        Task task = new Task("Задача №1", "Описание задачи №1");
        fileBackedManager.saveTask(task);
        assertEquals(fileBackedManager.getAllTasks(), loadFromFile(file).getAllTasks(),
                "Список задач после выгрузки не совпададает");
    }

    //Проверка восстановления Епик
    @Test
    public void chekLoadEpicStorage() {
        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        fileBackedManager.saveEpic(epic);
        assertEquals(fileBackedManager.getAllEpic(), loadFromFile(file).getAllEpic(),
                "Список задач после выгрузки не совпададает");
    }

    //Проверка восстановления подзадач
    @Test
    public void chekLoadSubTaskStorage() {
        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        fileBackedManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание подзадачи №1", 1);
        fileBackedManager.saveSubTask(subTask);
        assertEquals(fileBackedManager.getAllSubTask(), loadFromFile(file).getAllSubTask(),
                "Список задач после выгрузки не совпададает");
    }

    //Проверка восстановления Списка истории
    @Test
    public void chekLoadHistoryManager() {
        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        fileBackedManager.saveEpic(epic);
        assertEquals(fileBackedManager.getHistory(), loadFromFile(file).getHistory(),
                "Список задач после выгрузки не совпададает");
    }

    //Проверка восстановления Списка приоритизации
    @Test
    public void chekLoadprioritizedTasks() {
        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);

        Task task = new Task("Задача №1", "Описание задачи №1");
        fileBackedManager.saveTask(task);
        Task task2 = new Task("Задача №2", "Описание задачи №2");
        fileBackedManager.saveTask(task2);
        Task task3 = new Task("Задача №3", "Описание задачи №3");
        fileBackedManager.saveTask(task3);
        task3 = new Task("Задача №3", "Описание задачи №3",3, LocalDateTime.now(),1 );
        fileBackedManager.updateTask(task3);
        task = new Task("Задача №1",
                "Описание задачи №1",1, LocalDateTime.now().plusDays(1),1 );
        fileBackedManager.updateTask(task);

        System.out.println(loadFromFile(file).getPrioritizedTasks());
        assertEquals(fileBackedManager.getPrioritizedTasks(), loadFromFile(file).getPrioritizedTasks(),
                "Список задач после выгрузки не совпададает");
    }

    //Проверка Восстановления прогресса менеджера в CSV
    @Test
    public void checkLoadFileBackedTaskManager() {
        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedManager = loadFromFile(file);

        Task task = new Task("Задача №1", "Описание задачи №1");
        fileBackedManager.saveTask(task);
        Task task1 = new Task(
                "Задача №1", "Описание задачи №1", TaskStatus.NEW,1, task.getStartDateTime(),0);
        Map<Integer, Task> taskStorageTest = new HashMap<>();
        taskStorageTest.put(1,task1);
        assertEquals(fileBackedManager.getTask(1),taskStorageTest.get(1));
    }

    @Test
    public void checkListHistoryEmpty() {
        File file = new File("src/Test/dataTestNotHistory.csv");
        FileBackedTasksManager fileBackedManager = loadFromFile(file);

        Task task = new Task("Задача №1", "Описание задачи №1");
        fileBackedManager.saveTask(task);

        assertTrue(fileBackedManager.getHistory().isEmpty());
    }
}
