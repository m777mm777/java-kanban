import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int generatedId = 0;    //Начальное значение id

    //Колекции для хранения задач, епиков и подзадач эпиков
    private HashMap<Integer, Task> taskStorage = new HashMap<>();
    private HashMap<Integer, Epic> epicStorage = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskStorage = new HashMap<>();

    // Создание задачи TASK
    public Task saveTask(Task task) {
        int id = getGenerateId();
        task.setId(id);
        taskStorage.put(id, task);
        return task;
    }

    //Создание епика EPIK
    public Epic saveEpic(Epic epic) {
        int id = getGenerateId();
        epic.setId(id);
        epicStorage.put(id, epic);
        return epic;
    }

    //Создание подзадачи в определенный епик с проверкой наличия епика SUBTASK
    public SubTask saveSubTask(SubTask subTask) {
        if (subTask == null) {
            return null;
        }
        int id = getGenerateId();
        subTask.setEpicId(id);
        Epic epic = epicStorage.get(subTask.getEpiccId());
        if (epic != null) {
            subTaskStorage.put(id, subTask);
           // updateStatusEpic(epic);
            epic.setSubtaskIds(id);
            return subTask;
        }else {
            System.out.println("Нет такого эпика");
            return null;
        }
    }

    //Удаление задачи по id TASK
    public void removeTask(int id) {
        if(!taskStorage.containsKey(id)) {
            System.out.println("Задачи с таким id нет");
            return;
        }
        taskStorage.remove(id);
        System.out.println("Задача удалена");
    }

    //Удаление подзадачи по id SUBTASK
    public void removeSubTask(int id) {
        if(!subTaskStorage.containsKey(id)) {
            System.out.println("Подзадачи с таким id нет");
            return;
        }
        subTaskStorage.remove(id);
        System.out.println("Подзадача удалена");

    }

    //Удаление эпика по id и следовательно всех его подзадач EPIK
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
    public void removeAllTask (){
        taskStorage.clear();
    }

    //Удаление всех подзадач SUBTASK
    public void removeAllSubTask (){
        subTaskStorage.clear();
    }

    //Удаление всех епик и подзадачи тоже EPIK
    public void removeAllEpik (){
        subTaskStorage.clear();
        epicStorage.clear();
    }

    //Получение задачи по id TASK
    public Task getByTaskId(int id) {
        return taskStorage.get(id);
    }
    //Получение подзадачи по id SUBTASK
    public SubTask getBySubTaskId(int epicId) {
        return subTaskStorage.get(epicId);

    }
    //Получение епика по id EPIK
    public Epic getByEpikId(int epicId) {
        return epicStorage.get(epicId);
    }

    //Получение всех задач TASK
    public ArrayList<Task> getAllTasks() {
       return new ArrayList<>(taskStorage.values());
    }
    //Получение всех епиков EPIK
    public ArrayList<Epic> getAllEpik() {
        return new ArrayList<>(epicStorage.values());
    }
    //Получение всех подзадачь из всех эпиков без самих эпиков SUBTASK
    public ArrayList<Epic> getAllSubTask() {
        return new ArrayList(subTaskStorage.values());
    }

    //Получение всех задач одного Эпика
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
    public void updateTask(Task task, String status) {
        Task saved = taskStorage.get(task.getId());
        Task taskModofiedTask = getByTaskId(task.getId());
        taskModofiedTask.setStatus(status);
        if (saved == null) {
            return;
        }
        taskStorage.put(task.getId(), task);
    }

    //Обновление-Перезапись Епика с сохранением id
    public void updateEpic(Epic epic) {
        Task saved = taskStorage.get(epic.getId());
        if (saved == null) {
            return;
        }
        taskStorage.put(epic.getId(), epic);
    }

    //Обновление-Перезапись подзадачи с сохранением id для сверщика Епиков
    public void updateSubTask(SubTask subTask, String status) {
        SubTask saved = subTaskStorage.get(subTask.getId());
        SubTask subtaskModofied = getBySubTaskId(subTask.getId());
        subtaskModofied.setStatus(status);
        if (saved == null) {
            return;
        }
        subTaskStorage.put(subTask.getId(), subTask);
    }

    //Проверка и обновление статуса для всех Епиков EPIK
    public void checkStatusEpik() {
        String statusDone = "";
        String statusProgress = "";
        String statusNew = "";
        for (Epic epic : epicStorage.values()) {
            if (epic.getSubTaskId().isEmpty()) {
                Epic epicModofiedStatus = epic;
                epicModofiedStatus.updateStatusEpik("NEW");
                updateEpic(epicModofiedStatus);
            }else {
                for (int idSubtaskByEpik : epic.getSubTaskId()) {
                    SubTask subTask = subTaskStorage.get(idSubtaskByEpik);
                    String status = subTask.getStatus();
                    if (status.equals("IN_PROGRESS")) {
                        statusProgress = "IN_PROGRESS";
                    } else if (status.equals("DONE")) {
                        statusDone = "DONE";
                    }else {
                        statusNew = "NEW";
                    }
                }

                if (statusProgress.equals("IN_PROGRESS")) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.updateStatusEpik(statusProgress);
                    updateEpic(epicModofiedStatus);
                    statusDone = "";
                    statusProgress = "";
                    statusNew = "";

                } else if (statusDone.equals("DONE")&&statusNew.equals("NEW")) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.updateStatusEpik("IN_PROGRESS");
                    updateEpic(epicModofiedStatus);
                    statusDone = "";
                    statusProgress = "";
                    statusNew = "";

                } else if (statusDone.equals("DONE")) {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.updateStatusEpik("DONE");
                    updateEpic(epicModofiedStatus);
                    statusDone = "";
                    statusProgress = "";
                    statusNew = "";

                }else {
                    Epic epicModofiedStatus = epic;
                    epicModofiedStatus.updateStatusEpik("NEW");
                    updateEpic(epicModofiedStatus);
                    statusDone = "";
                    statusProgress = "";
                    statusNew = "";
                }

            }
        }
    }

    //Создание id
    private int getGenerateId() {
        return ++generatedId;
    }
}
