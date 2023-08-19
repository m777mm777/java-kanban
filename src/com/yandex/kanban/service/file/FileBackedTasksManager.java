package com.yandex.kanban.service.file;

import com.yandex.kanban.model.*;
import com.yandex.kanban.service.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) {

        FileBackedTasksManager taskManager = new FileBackedTasksManager();
        taskManager.loadFromFile(new File("src/data/data.csv"));
        System.out.println(taskManager.getEpik(4));
        System.out.println("История просмотров задач " + taskManager.getHistory());//Печать истории

        Task task1 = new Task("Задача №1", "Описание задачи №1");
        Task task2 = new Task("Задача №2", "Описание задачи №2");

        taskManager.saveTask(task1); //Создание задачи
        taskManager.saveTask(task2); //Создание задачи

        Epic epic1 = new Epic("Эпик №1", "Описание эпика №1");
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");

        taskManager.saveEpic(epic1); //Создание Епика
        taskManager.saveEpic(epic2); //Создание Епика

        SubTask subTask1 = new SubTask("Подзадача №1", "Описание Подзадачи №1", 4);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание Подзадачи №2", 3);
        SubTask subTask3 = new SubTask("Подзадача №3", "Описание Подзадачи №3", 3);

        taskManager.saveSubTask(subTask1); //Создание подзадачи и присвоение ее конкретному Епику
        taskManager.saveSubTask(subTask2); //Создание подзадачи и присвоение ее конкретному Епику
        taskManager.saveSubTask(subTask3); //Создание подзадачи и присвоение ее конкретному Епику

        taskManager.getTask(1);//Получение задачи должно отразится в истории
        taskManager.getTask(2);//Получение задачи должно отразится в истории
        taskManager.getTask(1);//Получение задачи должно отразится в истории
        taskManager.getTask(2);//Получение задачи должно отразится в истории

        taskManager.getEpik(3);//Получение Епика должно отразится в истории
        taskManager.getEpik(4);//Получение Епика должно отразится в истории
        taskManager.getEpik(3);//Получение Епика должно отразится в истории
        taskManager.getEpik(4);//Получение Епика должно отразится в истории

        taskManager.getSubTask(5);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(6);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(7);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(5);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(6);//Получение Подзадачи должно отразится в истории
        taskManager.getSubTask(7);//Получение Подзадачи должно отразится в истории

        System.out.println("История просмотров задач " + taskManager.getHistory());//Печать истории

        taskManager.removeTask(1);//Удаление задачи 1 и из истории

        System.out.println("История просмотров задач " + taskManager.getHistory());//Печать истории

        taskManager.removeEpik(3);//Удаление Епика и его Подзадач так же из истории
        taskManager.getSubTask(5);//Получение Подзадачи должно отразится в истории

        subTask1 = new SubTask("Подзадача №1", "Описание Подзадачи №1", TaskStatus.IN_PROGRESS, 5,4);
        taskManager.updateSubTask(subTask1);
        System.out.println("История просмотров задач " + taskManager.getHistory());//Печать истории

    }

    private static String fileName = "src/data/data.csv";
    private File file= new File(fileName);
    private static CSVFormatHandler handler = new CSVFormatHandler();

    //Сохранение прогресса менеджера в CSV
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(handler.getHeader());
            writer.newLine();

            for (Task task: taskStorage.values()) {
                writer.write(handler.toString(task));
                writer.newLine();
            }

            for (Epic epic: epicStorage.values()) {
                writer.write(handler.toString(epic));
                writer.newLine();
            }

            for (SubTask subTask: subTaskStorage.values()) {
                writer.write(handler.toString(subTask));
                writer.newLine();
            }

            writer.newLine();
            writer.write(handler.historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Не удается прочитать файл для записи");
        }
    }

    public FileBackedTasksManager loadFromFile (File file) {
       try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
           boolean isNextHistory = false;
           String line = bufferedReader.readLine();

            if (line == null) {
                System.out.println("Файл пуст");
            } else {
                while (bufferedReader.ready()) {
                    line = bufferedReader.readLine();
                    if (!line.equals("")) {
                        String[] parts = line.split(",");
                        if (line.equals(handler.getHeader())) {
                            continue;
                        }
                        if (!isNextHistory) {
                            TaskTupe tupe = TaskTupe.valueOf(parts[1]);

                            switch (tupe) {
                                case EPIK:
                                    Epic epic = (Epic) handler.fromString(line);
                                    epicStorage.put(epic.getId(), epic);
                                    break;
                                case SUBTASK:
                                    SubTask subTask = (SubTask) handler.fromString(line);
                                    subTaskStorage.put(subTask.getId(), subTask);
                                    Epic epicValue = epicStorage.get(subTask.getEpicId());
                                    epicValue.addSubtaskIds(subTask.getId());
                                    break;
                                case TASK:
                                    Task task = handler.fromString(line);
                                    taskStorage.put(task.getId(), task);
                                    break;
                            }
                        } else {
                            List<Integer> idHistory = handler.historyFromString(line);

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
                    } else {
                        isNextHistory = true;
                    }
                }
            }
        } catch (IOException e) {
           throw new ManagerSaveException("Не удалось считать данные из файла.");
       }
       for(Epic epic: epicStorage.values()) {
           checkStatusEpikId(epic);
       }
       return new FileBackedTasksManager();
    }

    // Создание задачи TASK
    @Override
    public Task saveTask(Task task) {
        int id = getGenerateId();
        task.setId(id);
        taskStorage.put(id, task);
        save();
        return task;
    }

    //Создание епика EPIK
    @Override
    public Epic saveEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        int id = getGenerateId();
        epic.setId(id);
        epicStorage.put(id, epic);
        save();
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
            save();
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
        historyManager.remove(id);
        System.out.println("Задача удалена");
        save();
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
        System.out.println("Подзадача и ее привязка к эпику удалена");
        save();
    }

    //Удаление эпика по id и следовательно всех его подзадач EPIK
    @Override
    public void removeEpik(int id) {
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
            subTaskStorage.remove(idSubTask);
            historyManager.remove(idSubTask);
        }
        epic.removeAllSubtaskIds();
        System.out.println("Удален Епик и его подзадачи если они были ");
        save();
    }

    //Удаление всех задач TASK
    @Override
    public void removeAllTask (){
        for (Task task : taskStorage.values()) {
            historyManager.remove(task.getId());
        }
        taskStorage.clear();
        save();
    }

    //Удаление всех подзадач SUBTASK
    @Override
    public void removeAllSubTask() {
        for (SubTask subTask: subTaskStorage.values()) {
            historyManager.remove(subTask.getEpicId());
        }
        subTaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.removeAllSubtaskIds();
            checkStatusEpikId(epic);
        }

        System.out.println("Все позадачи и их привязка по id к епикам удалены");
        save();
    }

    //Удаление всех епик и подзадачи тоже EPIK
    @Override
    public void removeAllEpik (){
        for (SubTask subTask: subTaskStorage.values()) {
            historyManager.remove(subTask.getId());
        }
        for (Epic epic: epicStorage.values()) {
            historyManager.remove(epic.getId());
        }
        subTaskStorage.clear();
        epicStorage.clear();
        save();
    }

    //Получение задачи по id TASK а так же добавление в историю просмотров
    @Override
    public Task getTask(int id) {
        Task task = taskStorage.get(id);
        historyManager.add(task);
        save();
        return task;
    }
    //Получение подзадачи по id SUBTASK а так же добавление в историю просмотров
    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTaskStorage.get(id);
        historyManager.add(subTask);
        save();
        return subTask;

    }
    //Получение епика по id EPIK а так же добавление в историю просмотров
    @Override
    public Epic getEpik(int id) {
        Epic epic = epicStorage.get(id);
        historyManager.add(epic);
        save();
        return epic;
    }

    //Обновление-Перезапись задач с сохранением id
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        taskStorage.put(task.getId(), task);
        historyManager.add(task);
        save();
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
        save();
    }

    //Обновление-Перезапись подзадачи с сохранением id для сверщика Епиков
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask == null) {
            return;
        }
        int id = subTask.getId();
        subTaskStorage.put(id, subTask);
        historyManager.add(subTask);
        Epic epic = epicStorage.get(subTask.getEpicId());
        checkStatusEpikId(epic);
        save();
    }

    private class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(final String message) {
            super(message);
        }
    }

}