package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int generatedIdGet() {
        return generatedId;
    }

    protected void setGeneratedId(int generatedId) {
        this.generatedId = generatedId;
    }

    //Начальное значение id
    protected int generatedId = 0;

    //Колекции для хранения задач, епиков и подзадач эпиков
    protected Map<Integer, Task> taskStorage = new HashMap<>();
    protected Map<Integer, Epic> epicStorage = new HashMap<>();
    protected Map<Integer, SubTask> subTaskStorage = new HashMap<>();
    private final Comparator<Task> taskComparator
           = Comparator.comparing(Task::getStartDateTime,
         Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    //Очистка всех задач
    @Override
    public void clean() {
        taskStorage.clear();
        epicStorage.clear();
        subTaskStorage.clear();
    }
    //Проверка пересечения задачи
    private void validate(Task task) {

        if (task.getStartDateTime() != null) {
            for (Task tasks : prioritizedTasks) {
            if (tasks.getStartDateTime() == null) {
                continue;
            }
            if (!tasks.getEndDateTime().isAfter(task.getStartDateTime())) {
                continue;
            }
            if (!tasks.getStartDateTime().isBefore(tasks.getEndDateTime())) {
                continue;
            }
            if (tasks.getId().equals(task.getId())) {
                continue;
            }
            throw new TaskValidateException("Задачи пересекаются");
        }
        }
    }

    //Получение списка задачь по приоритетности
    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    // Создание задачи TASK
    @Override
    public Task saveTask(Task task) {
        int id = getGenerateId();
        task.setId(id);
        validate(task);
        taskStorage.put(id, task);
        prioritizedTasks.add(task);
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
            validate(subTask);
            subTaskStorage.put(id, subTask);
            epic.addSubtaskIds(id);
            checkStatusEpikId(epic);
            chekEndDataTimeEpicBySubtask(epic);
            prioritizedTasks.add(subTask);
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
        Task task = taskStorage.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
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
        historyManager.remove(subTask.getEpicId());
        Integer epicId = subTask.getEpicId();
        Epic epic = epicStorage.get(epicId);
        epic.removeIdFromSubtaskIds(id);
        checkStatusEpikId(epic);
        chekEndDataTimeEpicBySubtask(epic);
        prioritizedTasks.remove(subTask);
        System.out.println("Подзадача и ее привязка к эпику удалена");
    }

    //Удаление эпика по id и следовательно всех его подзадач EPIC
    @Override
    public void removeEpic(int id) {
        Epic epic = epicStorage.remove(id);
        if(epic == null) {
            System.out.println("Епика с таким id нет");
            return;
        }
        historyManager.remove(id);
        List<Integer> subTaskIds = epic.getSubTaskId();
        for (Integer idSubTask : subTaskIds) {
            if(!subTaskStorage.containsKey(idSubTask)) {
                continue;
            }
            SubTask subTask = subTaskStorage.remove(idSubTask);
            historyManager.remove(idSubTask);
            prioritizedTasks.remove(subTask);
        }
        epic.removeAllSubtaskIds();
        System.out.println("Удален Епик и его подзадачи если они были ");
    }

    //Удаление всех задач TASK
    @Override
    public void removeAllTask (){
        for (Task task : taskStorage.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        taskStorage.clear();
    }

    //Удаление всех подзадач SUBTASK
    @Override
    public void removeAllSubTask() {
        for (SubTask subTask: subTaskStorage.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }
        subTaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.removeAllSubtaskIds();
            checkStatusEpikId(epic);
            chekEndDataTimeEpicBySubtask(epic);
        }
        System.out.println("Все позадачи и их привязка по id к епикам удалены");
    }

    //Удаление всех епик и подзадачи тоже EPIK
    @Override
    public void removeAllEpic(){
        for (SubTask subTask: subTaskStorage.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }
        for (Epic epic: epicStorage.values()) {
            historyManager.remove(epic.getId());
        }
        subTaskStorage.clear();
        epicStorage.clear();
    }

    //Получение задачи по id TASK а так же добавление в историю просмотров
    @Override
    public Task getTask(int id) {
        Task task = taskStorage.get(id);
        historyManager.add(task);
        return task;
    }

    //проверка есть ли Таск по id
    @Override
    public boolean chekTask(int id) {
        boolean i = taskStorage.containsKey(id);
        return i;
    }
    //Получение подзадачи по id SUBTASK а так же добавление в историю просмотров
    @Override
    public SubTask getSubTask(int epicId) {
        SubTask subTask = subTaskStorage.get(epicId);
        historyManager.add(subTask);
        return subTask;
    }

    //проверка есть ли SubTask по id
    @Override
    public boolean chekSubTask(int id) {
        boolean i = subTaskStorage.containsKey(id);
        return i;
    }

    //Получение епика по id EPIK а так же добавление в историю просмотров
    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epicStorage.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    //проверка есть ли Epic по id
    @Override
    public boolean chekEpic(int id) {
        boolean i = epicStorage.containsKey(id);
        return i;
    }

    //Получение всех задач TASK
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    //Получение всех епиков EPIK
    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epicStorage.values());
    }

    //Получение всех подзадачь из всех эпиков без самих эпиков SUBTASK
    @Override
    public List<SubTask> getAllSubTask() {
        return new ArrayList<>(subTaskStorage.values());
    }

    //Получение всех подзадач одного Эпика по id Эпика
    @Override
    public List<SubTask> getSubtaskByEpik(int id) {
        Epic epic = epicStorage.get(id);
        List<Integer> subTaskIds = epic.getSubTaskId();
        List<SubTask> subTaskByEpik = new ArrayList<>();
        for (Integer idBySubTaskByEpik : subTaskIds) {
            SubTask subTask = subTaskStorage.get(idBySubTaskByEpik);
            if (subTask == null) {
                System.out.println("Ошибка, ID подзадачи привязан а подзадачи нет");
                continue;
            }
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
        validate(task);
        prioritizedTasks.remove(getTask(task.getId()));
        prioritizedTasks.add(task);
        taskStorage.put(task.getId(), task);
        historyManager.add(task);
    }

    //Обновление-Перезапись Епика с сохранением id
    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        Integer epicId = epic.getId();
        Epic newEpic = epicStorage.get(epicId);
        newEpic.setName(epic.getName());
        newEpic.setDescription(epic.getDescription());
        historyManager.add(newEpic);
    }

    //Обновление-Перезапись подзадачи с сохранением id для сверщика Епиков
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask == null) {
            return;
        }
        int id = subTask.getId();
        validate(subTask);
        prioritizedTasks.remove(subTaskStorage.get(id));
        prioritizedTasks.add(subTask);
        subTaskStorage.put(id, subTask);
        historyManager.add(subTask);
        Epic epic = epicStorage.get(subTask.getEpicId());
        chekEndDataTimeEpicBySubtask(epic);
        checkStatusEpikId(epic);
    }

    //Проверка окончания времени епика исходя из сабтасков
    protected void chekEndDataTimeEpicBySubtask(Epic epic) {
        int duration = 0;
        LocalDateTime startEpic = null;
        LocalDateTime endEpic = null;
        for (Integer subtaskId : epic.getSubTaskId()) {
            SubTask currentSubtask = subTaskStorage.get(subtaskId);
            LocalDateTime startTimeSubtask = currentSubtask.getStartDateTime();
            LocalDateTime endTimeSubtask = currentSubtask.getEndDateTime();
            if (startTimeSubtask == null) {
                continue;
            }
            if (startEpic == null || startEpic.isAfter(startTimeSubtask)) {
                startEpic = startTimeSubtask;
            }
            if (endEpic == null || endEpic.isAfter(endTimeSubtask)) {
                endEpic = endTimeSubtask;
            }
            duration += currentSubtask.getDuration();
        }
        epic.setEndDateTime(endEpic);
        epic.setStartDateTime(startEpic);
        epic.setDuration(duration);
    }

    //Проверка и обновление статуса EPIK по id
    protected void checkStatusEpikId(Epic epic) {
        if (epic == null) {
            return;
        }
        if (epic.getSubTaskId().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allSubtaskIsNew = true;
        boolean allSubtaskIsDone = true;

        for (Integer epicSubtaskId : epic.getSubTaskId()) {
            TaskStatus status = subTaskStorage.get(epicSubtaskId).getStatus();
            if (!status.equals(TaskStatus.NEW)) {
                allSubtaskIsNew = false;
            }
            if (!status.equals(TaskStatus.DONE)) {
                allSubtaskIsDone = false;
            }
        }

        if (allSubtaskIsDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allSubtaskIsNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    //Создание id
    protected int getGenerateId() {
        return ++generatedId;
    }

    //Получение истории просмотров задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void loadFromFile(File file){
    }

}



