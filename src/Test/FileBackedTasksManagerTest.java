package Test;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.service.file.FileBackedTasksManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.yandex.kanban.service.file.FileBackedTasksManager.loadFromFile;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTasksManagerTest extends InMemoryTaskManagerTest <FileBackedTasksManager> {

    //Проверка Сохранение прогресса менеджера в CSV
    @Test
    public void checkSaveFileBackedTaskManager() {
        File file = new File("src/data/data.csv");
        File fileTest = new File("src/Test/dataTest.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);

        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        fileBackedManager.saveEpic(epic);

        String[] lines = new String[4];
        String[] linesTest = new String[4];

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            int i = 0;
            while (bufferedReader.ready()) {
                String linee = bufferedReader.readLine();
                lines[i] = linee;
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось считать данные из файла.");
        }

        try (BufferedReader bufferedReaderTest = new BufferedReader(new FileReader(fileTest, StandardCharsets.UTF_8))) {
            int i = 0;
            while (bufferedReaderTest.ready()) {
               String linee = bufferedReaderTest.readLine();
                linesTest[i] = linee;
                  i++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось считать данные из файла.");
        }
        Assertions.assertEquals(lines[1],linesTest[1]);
    }

    //Проверка Восстановления прогресса менеджера в CSV
    @Test
    public void checkLoadFileBackedTaskManager() {
        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedManager = loadFromFile(file);

        Task task = new Task("Задача №1", "Описание задачи №1");
        fileBackedManager.saveTask(task);
        Task task1 = new Task(
                "Задача №1", "Описание задачи №1", TaskStatus.NEW,1, LocalDateTime.now(),1);
        Map<Integer, Task> taskStorageTest = new HashMap<>();
        taskStorageTest.put(1,task1);
        Assertions.assertEquals(fileBackedManager.getTask(1),taskStorageTest.get(1));
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
