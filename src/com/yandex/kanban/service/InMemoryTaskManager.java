package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;

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

    private void prioritizedTask(Task task) {
        prioritizedTasks.add(task);
        validate();
    }

    //проверка пересечения времени
    private boolean checkTimeIntersection(Task task) {
        List<Task> tasks = List.copyOf(prioritizedTasks);
        if (tasks.size() > 0) {
            for (Task taskPrioritized : tasks) {
                    if (task.getStartDateTime().isBefore(taskPrioritized.getStartDateTime())
                            && !task.getEndDateTime().isBefore(taskPrioritized.getStartDateTime())) {
                        return true;
                    } else if (task.getStartDateTime().isAfter(taskPrioritized.getStartDateTime())
                            && task.getEndDateTime().isBefore(taskPrioritized.getStartDateTime())) {
                        return true;
                    }
            }
        } else {
            return false;
        }
        return false;
    }

    //Проверка пересечения задачи
    private void validate() {
        Set<Task> tasks = getPrioritizedTasks();
            for (Task task: tasks) {
            if (checkTimeIntersection(task)) {
                throw new TaskValidateException("Задачи пересекаются");
            }
        }
    }

    //Получение списка задачь по приоритетности
    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    //Восстановление истории просмотров
    public void loadHistory(List<Integer> idHistory) {
        for (int id : idHistory) {
            if (taskStorage.containsKey(id)) {
                historyManager.add(taskStorage.get(id));
            } else if (subTaskStorage.containsKey(id)) {
                historyManager.add(subTaskStorage.get(id));
            } else if (epicStorage.containsKey(id)) {
                historyManager.add(epicStorage.get(id));
            }
        }
    }

    // Создание задачи TASK
    @Override
    public Task saveTask(Task task) {
        int id = getGenerateId();
        task.setId(id);
        taskStorage.put(id, task);
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
            prioritizedTasks.remove(taskStorage.get(task.getId()));
        }
        taskStorage.clear();
    }

    //Удаление всех подзадач SUBTASK
    @Override
    public void removeAllSubTask() {
        for (SubTask subTask: subTaskStorage.values()) {
            historyManager.remove(subTask.getEpicId());
            prioritizedTasks.remove(subTaskStorage.get(subTask.getId()));
        }
        subTaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.removeAllSubtaskIds();
            checkStatusEpikId(epic);
        }
        System.out.println("Все позадачи и их привязка по id к епикам удалены");
    }

    //Удаление всех епик и подзадачи тоже EPIK
    @Override
    public void removeAllEpic(){
        for (SubTask subTask: subTaskStorage.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTaskStorage.get(subTask.getId()));
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
        prioritizedTasks.remove(taskStorage.get(task.getId()));
        taskStorage.put(task.getId(), task);
        historyManager.add(task);
        prioritizedTask(task);
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
        prioritizedTasks.remove(subTaskStorage.get(subTask.getId()));
        subTaskStorage.put(id, subTask);
        historyManager.add(subTask);
        prioritizedTask(subTask);
        Epic epic = epicStorage.get(subTask.getEpicId());
        checkStatusEpikId(epic);
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

}


