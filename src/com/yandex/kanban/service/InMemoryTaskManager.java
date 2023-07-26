package com.yandex.kanban.service;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    //Начальное значение id
    private int generatedId = 0;

    //Колекции для хранения задач, епиков и подзадач эпиков
    private Map<Integer, Task> taskStorage = new HashMap<>();
    private Map<Integer, Epic> epicStorage = new HashMap<>();
    private Map<Integer, SubTask> subTaskStorage = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    // Создание задачи TASK
    @Override
    public Task saveTask(Task task) {
        int id = getGenerateId();
        task.setId(id);
        taskStorage.put(id, task);
        return task;
    }

    //Создание епика EPIK
    @Override
    public Epic saveEpic(Epic epic) {
        int id = getGenerateId();
        epic.setId(id);
        epicStorage.put(id, epic);
        return epic;
    }

    //Создание подзадачи в определенный епик с проверкой наличия епика SUBTASK
    @Override
    public SubTask saveSubTask(SubTask subTask) {
        if (subTask == null) {
            return null;
        }
        int id = getGenerateId();
        subTask.setId(id);
        Epic epic = epicStorage.get(subTask.getEpicId());
        if (epic != null) {
            subTaskStorage.put(id, subTask);
            epic.addSubtaskIds(id);
            checkStatusEpik();
            return subTask;
        }else {
            System.out.println("Нет такого эпика");
            return null;
        }
    }

    //Удаление задачи по id TASK
    @Override
    public void removeTask(int id) {
        if(!taskStorage.containsKey(id)) {
            System.out.println("Задачи с таким id нет");
            return;
        }
        taskStorage.remove(id);
        System.out.println("Задача удалена");
    }

    //Удаление подзадачи по id SUBTASK
    @Override
    public void removeSubTask(Integer id) {

        SubTask subTask = subTaskStorage.remove(id);

        if(subTask == null) {
            System.out.println("Подзадачи с таким id нет");
            return;
        }
        Epic epic = epicStorage.get(subTask.getEpicId());
        epic.removeIdFromSubtaskIds(id);
        Integer epikId = subTask.getEpicId();
        checkStatusEpikId(epikId);
        System.out.println("Подзадача и ее привязка к эпику удалена");
    }

    //Удаление эпика по id и следовательно всех его подзадач EPIK
    @Override
    public void removeEpik(int id) {
        Epic epic = epicStorage.remove(id);
        if(epic == null) {
            System.out.println("Епика с таким id нет");
            return;
        }
        List<Integer> epicId = epic.getSubTaskId();
        for (Integer idSubTask : epicId) {
            if(!subTaskStorage.containsKey(idSubTask)) {
                continue;
            }
            subTaskStorage.remove(idSubTask);
        }
        epic.removeAllSubtaskIds();
        epicStorage.remove(id);
        System.out.println("Удален Епик и его подзадачи если они были ");
    }

    //Удаление всех задач TASK
    @Override
    public void removeAllTask (){
        taskStorage.clear();
    }

    //Удаление всех подзадач SUBTASK
    @Override
    public void removeAllSubTask() {
        subTaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.removeAllSubtaskIds();
        }
        checkStatusEpik();
        System.out.println("Все позадачи и их привязка по id к епикам удалены");
    }

    //Удаление всех епик и подзадачи тоже EPIK
    @Override
    public void removeAllEpik (){
        subTaskStorage.clear();
        epicStorage.clear();
    }

    //Получение задачи по id TASK а так же добавление в историю просмотров
    @Override
    public Task getByTaskId(int id) {
        Task task = taskStorage.get(id);
        historyManager.addHistory(task);
        return task;
    }
    //Получение подзадачи по id SUBTASK а так же добавление в историю просмотров
    @Override
    public SubTask getBySubTaskId(int epicId) {
        SubTask subTask = subTaskStorage.get(epicId);
        historyManager.addHistory(subTask);
        return subTask;

    }
    //Получение епика по id EPIK а так же добавление в историю просмотров
    @Override
    public Epic getByEpikId(int epicId) {
        Epic epic = epicStorage.get(epicId);
        historyManager.addHistory(epic);
        return epic;
    }

    //Получение всех задач TASK
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }
    //Получение всех епиков EPIK
    @Override
    public List<Task> getAllEpik() {
        return new ArrayList<>(epicStorage.values());
    }
    //Получение всех подзадачь из всех эпиков без самих эпиков SUBTASK
    @Override
    public List<Task> getAllSubTask() {
        return new ArrayList<>(subTaskStorage.values());
    }

    //Получение всех подзадач одного Эпика по id Эпика
    @Override
    public List<SubTask> getSubtaskByEpik(int id) {

        Epic epic = epicStorage.get(id);
        List<Integer> subTaskIds = epic.getSubTaskId();
        List<SubTask> subTaskByEpik = new ArrayList<>();

        for (Integer idBySubTaskByEpik : subTaskIds) {
            if (subTaskStorage.get(idBySubTaskByEpik) == null) {
                System.out.println("Ошибка, ID подзадачи привязан а подзадачи нет");
                continue;
            }
            SubTask subTask = subTaskStorage.get(idBySubTaskByEpik);
            subTaskByEpik.add(subTask);
        }
        return subTaskByEpik;
    }


    //Обновление-Перезапись задач с сохранением id
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        taskStorage.put(task.getId(), task);
    }


    //Обновление-Перезапись Епика с сохранением id
    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        Integer epicId = epic.getId();
        Epic values = epicStorage.get(epicId);
        values.setName(epic.getName());
        values.setDescription(epic.getDescription());
        epicStorage.put(values.getId(), values);

    }

    //Обновление-Перезапись подзадачи с сохранением id для сверщика Епиков
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask == null) {
            return;
        }
        int id = subTask.getId();
        subTaskStorage.put(id, subTask);
        Integer epikId = subTask.getEpicId();
        checkStatusEpikId(epikId);
    }

    //Проверка и обновление статуса EPIK по id
    private void checkStatusEpikId(Integer id) {

        TaskStatus statusDone = TaskStatus.NULL;
        TaskStatus statusProgress = TaskStatus.NULL;
        TaskStatus statusNew = TaskStatus.NULL;

        Epic epic = epicStorage.get(id);

        for (Integer idSubtaskByEpik : epic.getSubTaskId()) {
            SubTask subTask = subTaskStorage.get(idSubtaskByEpik);
            TaskStatus status = subTask.getStatus();
            if (status.equals(TaskStatus.IN_PROGRESS)) {
                statusProgress = TaskStatus.IN_PROGRESS;
            } else if (status.equals(TaskStatus.DONE)) {
                statusDone = TaskStatus.DONE;
            }else {
                statusNew = TaskStatus.NEW;
            }
        }

        if (statusProgress.equals(TaskStatus.IN_PROGRESS)) {
            Epic epicModofiedStatus = epic;
            epicModofiedStatus.setStatus(TaskStatus.IN_PROGRESS);
            updateEpic(epicModofiedStatus);
            statusDone = TaskStatus.NULL;
            statusProgress = TaskStatus.NULL;
            statusNew = TaskStatus.NULL;

        } else if (statusDone.equals(TaskStatus.DONE) && statusNew.equals(TaskStatus.NEW)) {
            Epic epicModofiedStatus = epic;
            epicModofiedStatus.setStatus(TaskStatus.IN_PROGRESS);
            updateEpic(epicModofiedStatus);
            statusDone = TaskStatus.NULL;
            statusProgress = TaskStatus.NULL;
            statusNew = TaskStatus.NULL;

        } else if (statusDone.equals(TaskStatus.DONE)) {
            Epic epicModofiedStatus = epic;
            epicModofiedStatus.setStatus(TaskStatus.DONE);
            updateEpic(epicModofiedStatus);
            statusDone = TaskStatus.NULL;
            statusProgress = TaskStatus.NULL;
            statusNew = TaskStatus.NULL;

        }else {
            Epic epicModofiedStatus = epic;
            epicModofiedStatus.setStatus(TaskStatus.NEW);
            updateEpic(epicModofiedStatus);
            statusDone = TaskStatus.NULL;
            statusProgress = TaskStatus.NULL;
            statusNew = TaskStatus.NULL;
        }

                }



    //Проверка и обновление статуса для всех Епиков EPIK
    private void checkStatusEpik() {
        TaskStatus statusDone = TaskStatus.NULL;
        TaskStatus statusProgress = TaskStatus.NULL;
        TaskStatus statusNew = TaskStatus.NULL;
        for (Epic epic : epicStorage.values()) {
            if (epic.getSubTaskId().isEmpty()) {
                Epic epicModofiedStatus = epic;
                epicModofiedStatus.setStatus(TaskStatus.NEW);
                updateEpic(epicModofiedStatus);
            }else {
                for (int idSubtaskByEpik : epic.getSubTaskId()) {
                    SubTask subTask = subTaskStorage.get(idSubtaskByEpik);
                    TaskStatus status = subTask.getStatus();
                    if (status.equals(TaskStatus.IN_PROGRESS)) {
                        statusProgress = TaskStatus.IN_PROGRESS;
                    } else if (status.equals(TaskStatus.DONE)) {
                        statusDone = TaskStatus.DONE;
                    }else {
                        statusNew = TaskStatus.NEW;
                    }
                }

                if (statusProgress.equals(TaskStatus.IN_PROGRESS)) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.setStatus(TaskStatus.IN_PROGRESS);
                    updateEpic(epicModofiedStatus);
                    statusDone = TaskStatus.NULL;
                    statusProgress = TaskStatus.NULL;
                    statusNew = TaskStatus.NULL;

                } else if (statusDone.equals(TaskStatus.DONE) && statusNew.equals(TaskStatus.NEW)) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.setStatus(TaskStatus.IN_PROGRESS);
                    updateEpic(epicModofiedStatus);
                    statusDone = TaskStatus.NULL;
                    statusProgress = TaskStatus.NULL;
                    statusNew = TaskStatus.NULL;

                } else if (statusDone.equals(TaskStatus.DONE)) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.setStatus(TaskStatus.DONE);
                    updateEpic(epicModofiedStatus);
                    statusDone = TaskStatus.NULL;
                    statusProgress = TaskStatus.NULL;
                    statusNew = TaskStatus.NULL;

                }else {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.setStatus(TaskStatus.NEW);
                    updateEpic(epicModofiedStatus);
                    statusDone = TaskStatus.NULL;
                    statusProgress = TaskStatus.NULL;
                    statusNew = TaskStatus.NULL;
                }

            }
        }
    }

    //Вывод ошибки
    private void error() {
        System.out.println("Задачи с этим id нет!");
    }

    //Создание id
    private int getGenerateId() {
        return ++generatedId;
    }

    //Получение истории просмотров задач
    @Override
    public List<Task> getHistory() {
       return historyManager.getHistory();
    }

}