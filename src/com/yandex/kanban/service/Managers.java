package com.yandex.kanban.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.kanban.service.file.FileBackedTasksManager;
import com.yandex.kanban.http.HttpTaskManager;
import com.yandex.kanban.http.LocalDateTimeAdapter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public final class Managers {

    private Managers() {

    }

    public static TaskManager getDefaultInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getDefaultFileBacked() throws IOException {
        return new FileBackedTasksManager(new File("src/data/data.csv"));

    }

    public static HttpTaskManager getDefault(int port, Boolean loads) throws IOException, InterruptedException {
        return new HttpTaskManager(port, loads);

    }

    public static HttpTaskManager getDefault(int port) throws IOException, InterruptedException {
        return new HttpTaskManager(port);

    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();

    }

    public static Gson gsonAdapter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }


}
