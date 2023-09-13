package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    //Начальное значение id
    protected int generatedId = 0;

    //Колекции для хранения задач, епиков и подзадач эпиков
    protected Map<Integer, Task> taskStorage = new HashMap<>();
    protected Map<Integer, Epic> epicStorage = new HashMap<>();
    protected Map<Integer, SubTask> subTaskStorage = new HashMap<>();
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartDateTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);
    protected final HistoryManager historyManager = Managers.getDefaultHistory();


    //Если это решение не годится подскажи плз как можно сюда добавить задачу если ее время начала null?
    private void prioritizedTask(Task task) {
        if (task.getStartDateTime() == null) {
            task.setStartDateTime(LocalDateTime.of(2030, 1, 1,1,task.getId()));
        }
        prioritizedTasks.add(task);
    }

    //Проверка пересечения задачи
    private void validate(Task task) {
        Set<Task> tasksPrioritized = getPrioritizedTasks();
        if (tasksPrioritized.size() < 1) {
        for (Task tasks : tasksPrioritized) {
            int i = 0;
            if (!tasks.getEndDateTime().isAfter(task.getStartDateTime())) {
                i++;
            } else {
                continue;
            }
            if (!tasks.getStartDateTime().isBefore(tasks.getEndDateTime())) {
                i++;
            } else {
                continue;
            }
            if (task.getId() == tasks.getId()) {
                continue;
            } else if (i == 2) {
                throw new TaskValidateException("Задачи пересекаются");
            }
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
        taskStorage.put(id, task);
        validate(task);
        prioritizedTask(task);
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
            checkStatusEpikId(epic);
            chekEndDataTimeEpicBySubtask(epic);
            validate(subTask);
            prioritizedTask(subTask);
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
            historyManager.remove(subTask.getEpicId());
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

    //Получение подзадачи по id SUBTASK а так же добавление в историю просмотров
    @Override
    public SubTask getSubTask(int epicId) {
        SubTask subTask = subTaskStorage.get(epicId);
        historyManager.add(subTask);
        return subTask;
    }

    //Получение епика по id EPIK а так же добавление в историю просмотров
    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epicStorage.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    //Получение всех задач TASK
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    //Получение всех епиков EPIK
    @Override
    public List<Task> getAllEpic() {
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
        prioritizedTasks.remove(taskStorage.get(task.getId()));
        prioritizedTask(task);
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
        prioritizedTask(subTask);
        subTaskStorage.put(id, subTask);
        historyManager.add(subTask);
        Epic epic = epicStorage.get(subTask.getEpicId());
        chekEndDataTimeEpicBySubtask(epic);
        checkStatusEpikId(epic);
    }

    //Проверка окончания времени епика исходя из сабтасков
    private void chekEndDataTimeEpicBySubtask(Epic epic) {
        LocalDateTime maxDataTimeEnd = LocalDateTime.of(2030, 1, 1, 0, 0, 0);
        for (Integer epicSubtaskId : epic.getSubTaskId()) {
        SubTask subTask = subTaskStorage.get(epicSubtaskId);
        if (subTask.getStartDateTime() != null) {
            if(subTask.getEndDateTime().isBefore(maxDataTimeEnd)) {
                maxDataTimeEnd = subTask.getEndDateTime();
                epic.setEndDateTime(maxDataTimeEnd);
            }
            } else {
            epic.setEndDateTime(maxDataTimeEnd);
        }
        }
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
        chekEndDataTimeEpicBySubtask(epic);
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

}


