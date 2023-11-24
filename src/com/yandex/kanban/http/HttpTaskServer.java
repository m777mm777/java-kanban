package com.yandex.kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import com.yandex.kanban.service.file.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {

    private final int PORT = 8080;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private Gson gson;
    private HttpServer server;

    public TaskManager manager;

    public HttpTaskServer() throws IOException {
        this.manager = Managers.getDefaultFileBacked();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.manager = taskManager;
        this.gson = Managers.gsonAdapter();
        this.server = HttpServer.create(new InetSocketAddress("LocalHost",PORT),0);
        server.createContext("/tasks", this::handler);
    }

    private void handler(HttpExchange exchange) {
        try {
            System.out.println("Processing /task-end-point " + exchange.getRequestURI());
            final String path = exchange.getRequestURI().getPath().replaceFirst("/tasks/", "");
            switch (path) {
                case "":
                    handlePrioritizedTask(exchange);
                    break;
                case "task":
                    handleTask(exchange);
                    break;
                case "subtask":
                    handleSubTask(exchange);
                    break;
                case "epic":
                    handleEpic(exchange);
                    break;
                case "history":
                    handleHistory(exchange);
                    break;
                case "subtask/epic":
                    handleSubTaskByEpic(exchange);
                    break;
                default:
            }
        } catch (IOException exception) {
            System.out.println("Ошибка при обработке запроса");
        } finally {
            exchange.close();
        }
    }

    private void handleHistory(HttpExchange exchange) throws IOException  {
        String metod = exchange.getRequestMethod();
        if (metod.equals("GET")) {
            String response = gson.toJson(manager.getHistory());
            sendText(exchange, response, 200);
        } else {
            System.out.println("Ожидаем метод GET, а получили " + metod);
            exchange.sendResponseHeaders(405,0);
        }
    }

    private void handlePrioritizedTask(HttpExchange exchange) throws IOException  {
        String metod = exchange.getRequestMethod();
        if (metod.equals("GET")) {
            String response = gson.toJson(manager.getPrioritizedTasks());
            sendText(exchange, response, 200);
        } else {
            System.out.println("Ожидаем метод GET, а получили " + metod);
            exchange.sendResponseHeaders(405,0);
        }
    }

    private void handleSubTaskByEpic(HttpExchange exchange) throws IOException  {
        String metod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        if (metod.equals("GET")) {
            String idEpic = query.substring(3);
            List<SubTask> subTasksByEpic = manager.getSubtaskByEpik(Integer.parseInt(idEpic));
            String response = gson.toJson(subTasksByEpic);
            sendText(exchange, response, 200);
        } else {
            System.out.println("Ожидаем метод GET, а получили " + metod);
            exchange.sendResponseHeaders(405,0);
        }
    }

    private void handleTask(HttpExchange exchange) throws IOException {

        String metod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String body = readText(exchange);

        switch (metod) {
            case "GET": {
                if (query != null) {
                    String idTask = query.substring(3);
                    Task task = manager.getTask(Integer.parseInt(idTask));
                    String response = gson.toJson(task);
                    sendText(exchange, response,200);
                    return;
                } else {
                    String response = gson.toJson(manager.getAllTasks());
                    sendText(exchange, response,200);
                    return;
                }
            }
            case "POST": {
                try {
                    if (!body.isEmpty()) {
                        Task task = gson.fromJson(body, Task.class);
                        if (manager.chekTask(task.getId())) {
                            manager.updateTask(task);
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            manager.saveTask(task);
                            exchange.sendResponseHeaders(200, 0);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
            case "DELETE": {
                if (query != null) {
                    String idTask = query.substring(3);
                    manager.removeTask(Integer.parseInt(idTask));
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    manager.removeAllTask();
                    exchange.sendResponseHeaders(200, 0);
                }
            }
        }
    }

    private void handleSubTask(HttpExchange exchange) throws IOException {

        String metod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String body = readText(exchange);

        switch (metod) {
            case "GET": {
                if (query != null) {
                    String idSubTask = query.substring(3);
                    SubTask subTask = manager.getSubTask(Integer.parseInt(idSubTask));
                    String response = gson.toJson(subTask);
                    sendText(exchange, response,200);
                    return;
                } else {
                    String response = gson.toJson(manager.getAllSubTask());
                    sendText(exchange, response,200);
                    return;
                }
            }
            case "POST": {
                try {
                    if (!body.isEmpty()) {
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    if (manager.chekSubTask(subTask.getId())) {
                        manager.updateSubTask(subTask);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        manager.saveSubTask(subTask);
                        exchange.sendResponseHeaders(200, 0);
                    }
                }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
            case "DELETE": {
                if (query != null) {
                    String idSubTask = query.substring(3);
                    manager.removeSubTask(Integer.parseInt(idSubTask));
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    manager.removeAllSubTask();
                    exchange.sendResponseHeaders(200, 0);
                }
            }
        }
    }

    private void handleEpic(HttpExchange exchange) throws IOException {

        String metod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String body = readText(exchange);

        switch (metod) {
            case "GET": {
                if (query != null) {
                    String idEpic = query.substring(3);
                    Epic epic = manager.getEpic(Integer.parseInt(idEpic));
                    String response = gson.toJson(epic);
                    sendText(exchange, response,200);
                    return;
                } else {
                    String response = gson.toJson(manager.getAllEpic());
                    sendText(exchange, response,200);
                    return;
                }
            }
            case "POST": {
                try {
                    if (!body.isEmpty()) {
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (manager.chekEpic(epic.getId())) {
                            manager.updateEpic(epic);
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            manager.saveEpic(epic);
                            exchange.sendResponseHeaders(200, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(400, 0);
                        return;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
            case "DELETE": {
                if (query != null) {
                    String idEpic = query.substring(3);
                    manager.removeEpic(Integer.parseInt(idEpic));
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    manager.removeAllEpic();
                    exchange.sendResponseHeaders(200, 0);
                }
            }
        }
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendText(HttpExchange httpExchange, String response,int responseCode) throws IOException {

        if(response.isBlank()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = response.getBytes(DEFAULT_CHARSET);
            httpExchange.getResponseHeaders().add("Content-Type", "Application/json");
            httpExchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        httpExchange.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        final HttpTaskServer server = new HttpTaskServer(fileBackedTasksManager);
        server.start();


        server.manager.loadFromFile(file);

        System.out.println("history" + server.manager.getHistory());
        Task task1 = new Task("Задача №1", "Описание задачи №1");
        server.manager.saveTask(task1); //Создание задачи

        Task task2 = new Task("Задача №2", "Описание задачи №2");
        server.manager.saveTask(task2); //Создание задачи

        Epic epic1 = new Epic("Эпик №1", "Описание эпика №1");
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");

        server.manager.saveEpic(epic1); //Создание Епика
        server.manager.saveEpic(epic2); //Создание Епика

        SubTask subTask1 = new SubTask("Подзадача №1", "Описание Подзадачи №1", 4);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание Подзадачи №2", 3);
        SubTask subTask3 = new SubTask("Подзадача №3", "Описание Подзадачи №3", 3);

        server.manager.saveSubTask(subTask1); //Создание подзадачи и присвоение ее конкретному Епику
        server.manager.saveSubTask(subTask2); //Создание подзадачи и присвоение ее конкретному Епику
        server.manager.saveSubTask(subTask3); //Создание подзадачи и присвоение ее конкретному Епику
        server.manager.getTask(1);

    }

    public void start(){

        server.start();
        System.out.println("Запуск");
    }

    public void stop(int delay){

        server.stop(delay);
        System.out.println("Остановка");
    }
}
