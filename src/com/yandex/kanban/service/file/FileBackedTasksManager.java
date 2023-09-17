package com.yandex.kanban.service.file;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.service.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;
    private final static CSVFormatHandler handler = new CSVFormatHandler();

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {

        File file = new File("src/data/data.csv");
        FileBackedTasksManager fileBackedManager = loadFromFile(file);

        System.out.println("История просмотров задач " + fileBackedManager.getHistory());//Печать истории

        Task task1 = new Task("Задача №1", "Описание задачи №1");
        Task task2 = new Task("Задача №2", "Описание задачи №2");

        fileBackedManager.saveTask(task1); //Создание задачи
        fileBackedManager.saveTask(task2); //Создание задачи

        Epic epic1 = new Epic("Эпик №1", "Описание эпика №1");
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");

        fileBackedManager.saveEpic(epic1); //Создание Епика
        fileBackedManager.saveEpic(epic2); //Создание Епика

        SubTask subTask1 = new SubTask("Подзадача №1", "Описание Подзадачи №1", 4);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание Подзадачи №2", 3);
        SubTask subTask3 = new SubTask("Подзадача №3", "Описание Подзадачи №3", 3);

        fileBackedManager.saveSubTask(subTask1); //Создание подзадачи и присвоение ее конкретному Епику
        fileBackedManager.saveSubTask(subTask2); //Создание подзадачи и присвоение ее конкретному Епику
        fileBackedManager.saveSubTask(subTask3); //Создание подзадачи и присвоение ее конкретному Епику

        fileBackedManager.getTask(1);//Получение задачи должно отразится в истории
        fileBackedManager.getTask(2);//Получение задачи должно отразится в истории
        fileBackedManager.getTask(1);//Получение задачи должно отразится в истории
        fileBackedManager.getTask(2);//Получение задачи должно отразится в истории
        fileBackedManager.getEpic(3);//Получение Епика должно отразится в истории
        fileBackedManager.getEpic(4);//Получение Епика должно отразится в истории
        fileBackedManager.getEpic(3);//Получение Епика должно отразится в истории
        fileBackedManager.getEpic(4);//Получение Епика должно отразится в истории

        fileBackedManager.getSubTask(5);//Получение Подзадачи должно отразится в истории
        fileBackedManager.getSubTask(6);//Получение Подзадачи должно отразится в истории
        fileBackedManager.getSubTask(7);//Получение Подзадачи должно отразится в истории
        fileBackedManager.getSubTask(5);//Получение Подзадачи должно отразится в истории
        fileBackedManager.getSubTask(6);//Получение Подзадачи должно отразится в истории
        fileBackedManager.getSubTask(7);//Получение Подзадачи должно отразится в истории

        fileBackedManager.removeTask(1);//Удаление задачи 1 и из истории

        fileBackedManager.removeEpic(3);//Удаление Епика и его Подзадач так же из истории
        fileBackedManager.getSubTask(5);//Получение Подзадачи должно отразится в истории

        System.out.println("приоритет лист 1" + fileBackedManager.getPrioritizedTasks());
        subTask1 = new SubTask(
                "Подзадача №1",
                "Описание Подзадачи №1",
                TaskStatus.IN_PROGRESS, 5, LocalDateTime.now().plusDays(7),1,4);

        fileBackedManager.updateSubTask(subTask1);


        System.out.println("приоритет лист 2" + fileBackedManager.getPrioritizedTasks());
        FileBackedTasksManager loadFromfFile = FileBackedTasksManager.loadFromFile(file);

        System.out.println("История просмотров задач " + fileBackedManager.getHistory());//Печать истории
        System.out.println("Проверка задач: " + loadFromfFile.taskStorage.equals(fileBackedManager.taskStorage));
        System.out.println("Проверка епиков: " + loadFromfFile.epicStorage.equals(fileBackedManager.epicStorage));
        System.out.println("Проверка подзадач " + loadFromfFile.subTaskStorage.equals(fileBackedManager.subTaskStorage));

        System.out.println("приоритет лист" + fileBackedManager.getPrioritizedTasks());

    }

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
            writer.newLine();

            writer.newLine();
            writer.write(handler.prioritizedTasksToString(prioritizedTasks));

        } catch (IOException e) {
            throw new ManagerSaveException("Не удается прочитать файл для записи");
        }
    }

    //Восстановление истории просмотров
    private void loadHistory(List<Integer> idHistory) {
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

    //Восстановление приоритетности
    private void loadPrioritizedTasks(List<Integer> idPrioritizedTasks) {
        for (int id : idPrioritizedTasks) {
            if (taskStorage.containsKey(id)) {
                prioritizedTasks.add(taskStorage.get(id));
            } else if (subTaskStorage.containsKey(id)) {
                prioritizedTasks.add(subTaskStorage.get(id));
            } else if (epicStorage.containsKey(id)) {
                prioritizedTasks.add(epicStorage.get(id));
            }
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            int nextCommand = 0;
            String line = bufferedReader.readLine();

            if (line == null) {
                System.out.println("Файл пуст");
            } else {
                while (bufferedReader.ready()) {
                    line = bufferedReader.readLine();
                    if (!line.equals("")) {
                        if (line.equals(handler.getHeader())) {
                            continue;
                        }
                        if (nextCommand < 1) {
                            Task task = handler.fromString(line);

                            switch (task.getType()) {
                                case EPIC:
                                    Epic epic = (Epic) task;
                                    fileBackedTasksManager.epicStorage.put(epic.getId(), epic);
                                    break;
                                case SUBTASK:
                                    SubTask subTask = (SubTask) task;
                                    fileBackedTasksManager.subTaskStorage.put(subTask.getId(), subTask);
                                    Epic epicValue = fileBackedTasksManager.epicStorage.get(subTask.getEpicId());
                                    epicValue.addSubtaskIds(subTask.getId());
                                    fileBackedTasksManager.chekEndDataTimeEpicBySubtask(epicValue);
                                    break;
                                case TASK:
                                    fileBackedTasksManager.taskStorage.put(task.getId(), task);
                                    break;
                            }
                        } else {
                            switch (nextCommand) {
                                case 1:
                                    List<Integer> idHistory = handler.historyFromString(line);
                                    fileBackedTasksManager.loadHistory(idHistory);
                                    break;
                                case 2:
                                    List<Integer> idPrioritized = handler.StringToPrioritizedTasks(line);
                                    fileBackedTasksManager.loadPrioritizedTasks(idPrioritized);
                                    break;
                            }
                        }
                    } else {
                        nextCommand++;
                    }
                }
            }
            return fileBackedTasksManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }
    }

    // Создание задачи TASK
    @Override
    public Task saveTask(Task task) {
        Task newTask = super.saveTask(task);
        save();
        return newTask;
    }

    //Создание епика EPIK
    @Override
    public Epic saveEpic(Epic epic) {
        Epic newEpic = super.saveEpic(epic);
        save();
        return newEpic;
    }

    //Создание подзадачи в определенный епик с проверкой наличия епика SUBTASK
    @Override
    public SubTask saveSubTask(SubTask subTask) {
        SubTask newSubtask = super.saveSubTask(subTask);
        save();
        return newSubtask;
    }

    //Удаление задачи по id TASK
    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    //Удаление подзадачи по id SUBTASK
    @Override
    public void removeSubTask(Integer id) {
        super.removeSubTask(id);
        save();
    }

    //Удаление эпика по id и следовательно всех его подзадач EPIK
    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    //Удаление всех задач TASK
    @Override
    public void removeAllTask (){
        super.removeAllTask();
        save();
    }

    //Удаление всех подзадач SUBTASK
    @Override
    public void removeAllSubTask() {
        super.removeAllSubTask();
        save();
    }

    //Удаление всех епик и подзадачи тоже EPIK
    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    //Получение задачи по id TASK а так же добавление в историю просмотров
    @Override
    public Task getTask(int id) {
        Task newTask = super.getTask(id);
        save();
        return newTask;
    }

    //Получение подзадачи по id SUBTASK а так же добавление в историю просмотров
    @Override
    public SubTask getSubTask(int id) {
        SubTask newSubtask = super.getSubTask(id);
        save();
        return newSubtask;
    }

    //Получение епика по id EPIK а так же добавление в историю просмотров
    @Override
    public Epic getEpic(int id) {
        Epic newEpic = super.getEpic(id);
        save();
        return newEpic;
    }

    //Обновление-Перезапись задач с сохранением id
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    //Обновление-Перезапись Епика с сохранением id
    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    //Обновление-Перезапись подзадачи с сохранением id для сверщика Епиков
    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    private static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(final String message) {
            super(message);
        }
    }

}