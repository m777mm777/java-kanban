

import java.util.ArrayList;
        import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{

    //Начальное значение id
    private int generatedId = 0;

    //Колекции для хранения задач, епиков и подзадач эпиков
    private HashMap<Integer, Task> taskStorage = new HashMap<>();
    private HashMap<Integer, Epic> epicStorage = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskStorage = new HashMap<>();
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
        subTask.setSubTaskId(id);
        Epic epic = epicStorage.get(subTask.getEpiccId());
        if (epic != null) {
            subTaskStorage.put(id, subTask);
            epic.setSubtaskIds(id);
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
    public void removeSubTask(int id) {
        if(!subTaskStorage.containsKey(id)) {
            System.out.println("Подзадачи с таким id нет");
            return;
        }
        SubTask subTask = subTaskStorage.get(id);
        Epic epic = epicStorage.get(subTask.getEpiccId());
        epic.removeIdFromSubtaskIds(id);
        subTaskStorage.remove(id);
        checkStatusEpik();
        System.out.println("Подзадача и ее привязка к эпику удалена");
    }

    //Удаление эпика по id и следовательно всех его подзадач EPIK
    @Override
    public void removeEpik(int id) {
        if(!epicStorage.containsKey(id)) {
            System.out.println("Епика с таким id нет");
            return;
        }
        Epic epic = epicStorage.get(id);
        ArrayList<Integer> epicId = epic.getSubTaskId();
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
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }
    //Получение всех епиков EPIK
    @Override
    public ArrayList<Epic> getAllEpik() {
        return new ArrayList<>(epicStorage.values());
    }
    //Получение всех подзадачь из всех эпиков без самих эпиков SUBTASK
    @Override
    public ArrayList<Epic> getAllSubTask() {
        return new ArrayList(subTaskStorage.values());
    }

    //Получение всех задач одного Эпика
    @Override
    public HashMap<Integer, HashMap<Integer, SubTask>> getSubtaskByEpik(ArrayList<Integer> subTaskId) {

        HashMap<Integer, HashMap<Integer, SubTask>> subTaskByEpik = new HashMap<>();

        for (Integer id : subTaskId) {
            if (!subTaskByEpik.containsKey(id)) {
                subTaskByEpik.put(id, new HashMap<>());
            }
            HashMap<Integer, SubTask> epikBySubtask = subTaskByEpik.get(id);

            epikBySubtask.put(id,subTaskStorage.get(id));
        }
        return subTaskByEpik;
    }


    //Обновление-Перезапись задач с сохранением id
    @Override
    public void updateTask(Task task, TaskStatus status) {
        Task saved = taskStorage.get(task.getId());
        Task taskModofiedTask = getByTaskId(task.getId());
        taskModofiedTask.setStatus(status);
        if (saved == null) {
            return;
        }
        taskStorage.put(task.getId(), task);
    }

    //Обновление-Перезапись Епика с сохранением id
    @Override
    public void updateEpic(Epic epic) {
        Task saved = epicStorage.get(epic.getId());
        if (saved == null) {
            return;
        }
        epicStorage.put(epic.getId(), epic);
    }

    //Обновление-Перезапись подзадачи с сохранением id для сверщика Епиков
    @Override
    public void updateSubTask(SubTask subTask, TaskStatus status) {
        SubTask saved = subTaskStorage.get(subTask.getId());
        SubTask subtaskModofied = getBySubTaskId(subTask.getId());
        subtaskModofied.setStatus(status);
        if (saved == null) {
            return;
        }
        subTaskStorage.put(subTask.getId(), subTask);
        checkStatusEpik();
    }

    //Проверка и обновление статуса для всех Епиков EPIK
    @Override
    public void checkStatusEpik() {
        String statusDone = "";
        String statusProgress = "";
        String statusNew = "";
        for (Epic epic : epicStorage.values()) {
            if (epic.getSubTaskId().isEmpty()) {
                Epic epicModofiedStatus = epic;
                epicModofiedStatus.updateStatusEpik(TaskStatus.NEW);
                updateEpic(epicModofiedStatus);
            }else {
                for (int idSubtaskByEpik : epic.getSubTaskId()) {
                    SubTask subTask = subTaskStorage.get(idSubtaskByEpik);
                    TaskStatus status = subTask.getStatus();
                    if (status.equals(TaskStatus.IN_PROGRESS)) {
                        statusProgress = "IN_PROGRESS";
                    } else if (status.equals(TaskStatus.DONE)) {
                        statusDone = "DONE";
                    }else {
                        statusNew = "NEW";
                    }
                }

                if (statusProgress.equals(TaskStatus.IN_PROGRESS)) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.updateStatusEpik(TaskStatus.IN_PROGRESS);
                    updateEpic(epicModofiedStatus);
                    statusDone = "";
                    statusProgress = "";
                    statusNew = "";

                } else if (statusDone.equals(TaskStatus.DONE) && statusNew.equals(TaskStatus.NEW)) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.updateStatusEpik(TaskStatus.IN_PROGRESS);
                    updateEpic(epicModofiedStatus);
                    statusDone = "";
                    statusProgress = "";
                    statusNew = "";

                } else if (statusDone.equals(TaskStatus.DONE)) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.updateStatusEpik(TaskStatus.DONE);
                    updateEpic(epicModofiedStatus);
                    statusDone = "";
                    statusProgress = "";
                    statusNew = "";

                }else {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.updateStatusEpik(TaskStatus.NEW);
                    updateEpic(epicModofiedStatus);
                    statusDone = "";
                    statusProgress = "";
                    statusNew = "";
                }

            }
        }
    }

    //Вывод ошибки
    @Override
    public void error() {
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
