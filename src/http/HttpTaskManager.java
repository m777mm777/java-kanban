package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.file.FileBackedTasksManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    public static final String TASKS_KEY = "tasks";
    public static final String SUBTASKS_KEY = "subtasks";
    public static final String EPIC_KEY = "epics";
    public static final String HISTORY_KEY = "history";
    public KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(int port) throws IOException, InterruptedException {

        super(null);
        this.client = new KVTaskClient(port);
        this.gson = Managers.gsonAdapter();

    }

    @Override
    public void save() {

        String jsonTask = gson.toJson(getAllTasks());
        client.put(TASKS_KEY, jsonTask);

        String jsonSubTask = gson.toJson(getAllSubTask());
        client.put(SUBTASKS_KEY, jsonSubTask);

        String jsonEpic = gson.toJson(getAllEpic());
        client.put(EPIC_KEY, jsonEpic);

        List<Integer> historyIds = historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        String historyJson = gson.toJson(historyIds);
        client.put(HISTORY_KEY, historyJson);
    }


    public void load() throws IOException, InterruptedException {

        Type tasksType = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(client.load(TASKS_KEY), tasksType);
        if (tasks != null) {
            tasks.forEach(task -> {
                int id = task.getId();
                this.taskStorage.put(id, task);
                this.prioritizedTasks.add(task);

            });
        }

        Type subtasksType = new TypeToken<List<SubTask>>(){}.getType();
        List<SubTask> subtasks = gson.fromJson(client.load(SUBTASKS_KEY), subtasksType);
        if (subtasks != null) {
            subtasks.forEach(subtask -> {
                int id = subtask.getId();
                this.subTaskStorage.put(id, subtask);
                this.prioritizedTasks.add(subtask);

            });
        }

        Type epicsType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(client.load(EPIC_KEY), epicsType);
        if (epics != null) {
            epics.forEach(epic -> {
                int id = epic.getId();
                this.epicStorage.put(id, epic);
                this.prioritizedTasks.add(epic);

            });
        }

        String responseBody = client.load(HISTORY_KEY);
        String body = responseBody.substring(1, responseBody.length()-1);
        String[] history = body.split(",");
        if (history != null) {
            for (String ids:history) {
                int id = Integer.parseInt(ids);
                historyManager.add(this.searchTask(id));
            }
        }
    }

    protected Task searchTask(Integer id) {
        if (taskStorage.get(id) != null) {
            return taskStorage.get(id);
        }
        if (epicStorage.get(id) != null) {
            return epicStorage.get(id);
        }
        return epicStorage.get(id);
    }
}
