package Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.file.FileBackedTasksManager;
import http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    Gson gson = Managers.gsonAdapter();
    HttpTaskServer server;
    FileBackedTasksManager manager;

    @BeforeEach
    void startHttpTaskManager() throws IOException {
        server = new HttpTaskServer();
        server.start();
        manager = Managers.getDefaultFileBacked();
    }

    @AfterEach
    void stopHttpTaskManagerTest() {
        server.stop(0);
    }

    @Test
    void createTaskAndGetTask() throws IOException, InterruptedException {

        Task task = new Task("Task 1", "Описание задачи 1");
        String str = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(str))
                .header("Content-Type", "application/json")
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Task 2", "Описание задачи 2");
        String str2 = gson.toJson(task2);
        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(str2))
                .header("Content-Type", "application/json")
                .build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/tasks/task");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {}.getType();

        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .GET()
                .build();
        HttpResponse<String> responseGetTaskById = client.send(request1, HttpResponse.BodyHandlers.ofString());
        Type taskTypeGetTaskById = new TypeToken<Task>() {}.getType();

        Task taskGetById = gson.fromJson(responseGetTaskById.body(), taskTypeGetTaskById);
        List<Task> taskList = gson.fromJson(response.body(), taskType);
        List<Task> tasks = server.manager.getAllTasks();

        assertEquals(1, taskGetById.getId(), "Задачи не равны");
        assertEquals(taskList, tasks, "Списки задач не равны");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
        assertNotNull(taskList, "Задачи не возвращаются");
          assertEquals(2, taskList.size(), "Неверное количество задач");
    }

    @Test
    void createEpicAndGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Описание Епика 1");
        String str = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(str))
                .header("Content-Type", "application/json")
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic2 = new Epic("Epic 2", "Описание Эпика 2");
        String str2 = gson.toJson(epic2);
        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(str2))
                .header("Content-Type", "application/json")
                .build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Epic>>() {}.getType();

        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest requestGetTaskById = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .GET()
                .build();
        HttpResponse<String> responseGetTaskById = client.send(requestGetTaskById, HttpResponse.BodyHandlers.ofString());
        Type taskTypeGetEpicById = new TypeToken<Epic>() {}.getType();

        Epic EpicGetById = gson.fromJson(responseGetTaskById.body(), taskTypeGetEpicById);
        List<Epic> epicsList = gson.fromJson(response.body(), taskType);
        List<Epic> epics = server.manager.getAllEpic();

        assertEquals(1, EpicGetById.getId(), "Задачи не равны");
        assertEquals(epicsList, epics, "Задачи не равны");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
        assertNotNull(epicsList, "Задачи не возвращаются");
        assertEquals(2, epicsList.size(), "Неверное количество задач");
    }

    @Test
    void createSubTaskAndGetSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Описание Епика 1");
        String str = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(str))
                .header("Content-Type", "application/json")
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask = new SubTask("SubTask ", "Описание Сабтаск",1);
        String str2 = gson.toJson(subTask);
        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(str2))
                .header("Content-Type", "application/json")
                .build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        SubTask subTask2 = new SubTask("SubTask 2", "Описание Сабтаск 2",1);
        String str3 = gson.toJson(subTask2);
        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .POST(HttpRequest.BodyPublishers.ofString(str3))
                .header("Content-Type", "application/json")
                .build();
        client3.send(request3, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Type subTaskType = new TypeToken<List<SubTask>>() {}.getType();

        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest requestGetTaskById = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .GET()
                .build();
        HttpResponse<String> responseGetTaskById = client.send(requestGetTaskById, HttpResponse.BodyHandlers.ofString());
        Type taskTypeGetSubTaskById = new TypeToken<SubTask>() {}.getType();

        SubTask subTaskGetById = gson.fromJson(responseGetTaskById.body(), taskTypeGetSubTaskById);
        List<SubTask> subTaskList = gson.fromJson(response.body(), subTaskType);
        List<SubTask> subTasks = server.manager.getAllSubTask();

        assertEquals(2, subTaskGetById.getId(), "Задачи не равны");
        assertEquals(subTaskList, subTasks, "Задачи не равны");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
        assertNotNull(subTaskList, "Задачи не возвращаются");
        assertEquals(2, subTaskList.size(), "Неверное количество задач");
    }

    @Test
    void removeTaskById() throws IOException, InterruptedException {

        Task task = new Task("Task 1", "Описание задачи 1");
        server.manager.saveTask(task);

        Task task2 = new Task("Task 2", "Описание задачи 2");
        server.manager.saveTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(null, server.manager.getTask(1), "Задача есть а быть не доллжно");
        assertEquals(1, server.manager.getAllTasks().size(), "Задач не 1");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
    }

    @Test
    void removeallTask() throws IOException, InterruptedException {

        Task task = new Task("Task 1", "Описание задачи 1");
        server.manager.saveTask(task);

        Task task2 = new Task("Task 2", "Описание задачи 2");
        server.manager.saveTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, server.manager.getAllTasks().size(), "Задач не 0");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
    }

    @Test
    void removeEpicById() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic 1", "Описание Епика 1");
        server.manager.saveEpic(epic);

        Epic epic2 = new Epic("Epic 2", "Описание Епика 2");
        server.manager.saveEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(null, server.manager.getEpic(1), "Задача есть а быть не доллжно");
        assertEquals(1, server.manager.getAllEpic().size(), "Задач не 1");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
    }

    @Test
    void removeAllEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic 1", "Описание Епика 1");
        server.manager.saveEpic(epic);

        Epic epic2 = new Epic("Epic 2", "Описание Епика 2");
        server.manager.saveEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, server.manager.getAllEpic().size(), "Задач не 1");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
    }

    @Test
    void removeSubTaskById() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic 1", "Описание Епика 1");
        server.manager.saveEpic(epic);

        SubTask subTask = new SubTask("SubTask ", "Описание Сабтаск",1);
        server.manager.saveSubTask(subTask);

        SubTask subTask2 = new SubTask("SubTask 2", "Описание Сабтаск 2",1);
        server.manager.saveSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(null, server.manager.getSubTask(2), "Задача есть а быть не доллжно");
        assertEquals(1, server.manager.getAllSubTask().size(), "Задач не 1");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
    }

    @Test
    void removeAllSubTask() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic 1", "Описание Епика 1");
        server.manager.saveEpic(epic);

        SubTask subTask = new SubTask("SubTask ", "Описание Сабтаск",1);
        server.manager.saveSubTask(subTask);

        SubTask subTask2 = new SubTask("SubTask 2", "Описание Сабтаск 2",1);
        server.manager.saveSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI urlGetTaskById = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlGetTaskById)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(0, server.manager.getAllSubTask().size(), "Задач не 0");
        assertEquals(200, response.statusCode(), "Запрос неуспешен, неверный статус-код");
    }

}
