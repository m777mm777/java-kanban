package com.yandex.kanban.service.file;

import com.yandex.kanban.model.*;
import com.yandex.kanban.service.HistoryManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CSVFormatHandler {
    private static String DELIMITER = ",";

//Из задачи в строку для записи
    String toString(Task task) {
        String result = task.getId() + DELIMITER
                + task.getType() + DELIMITER
                + task.getName() + DELIMITER
                + task.getStatus() + DELIMITER
                + task.getDescription() + DELIMITER
                + task.getStartDateTime() + DELIMITER
                + task.getDuration() + DELIMITER;
        if (task.getType() == TaskType.SUBTASK) {
            result = result + ((SubTask) task).getEpicId();
        }

        return result;
    }

    //Из сохраненной строки в задачу
    Task fromString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType tupe = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startDateTime;

        if (parts[5].equals("null")) {
            startDateTime = null;
        }else {
            startDateTime = LocalDateTime.parse(parts[5]);
        }
        Integer duration = Integer.valueOf(parts[6]);

        if (tupe == TaskType.EPIC) {
            Epic epic = new Epic(name, description, status, id, startDateTime, duration);
            return epic;
        } else if (tupe == TaskType.SUBTASK) {
            int epikId = Integer.parseInt(parts[7]);
            SubTask subTask = new SubTask(name, description, status, id, startDateTime, duration, epikId);
            return subTask;
        }else {
            Task task = new Task(name, description, status, id, startDateTime, duration);
            return task;
        }
    }

    //из истории просмотров в строку с id для сохранения
    String historyToString(HistoryManager manager) {
        List<String> result = new ArrayList<>();
        for(Task task: manager.getHistory()) {
            result.add(String.valueOf(task.getId()));
        }
        return String.join(DELIMITER,result);
    }

    //из сохраненной строки в историю просмотров
    List<Integer> historyFromString(String line) {
        List<Integer> historyTasks = new ArrayList<>();
        String[] parts = line.split(",");
        for (String part : parts) {
            int id = Integer.parseInt(part);
            historyTasks.add(id);
        }
        return historyTasks;
    }

    //из списка приоритетности в строку с id для сохранения
    String prioritizedTasksToString(Set<Task> prioritizedTasks) {
        List<String> result = new ArrayList<>();
        for(Task task: prioritizedTasks) {
            result.add(String.valueOf(task.getId()));
        }
        return String.join(DELIMITER,result);
    }

    //из строки в список id приоритетности
    List<Integer> StringToPrioritizedTasks(String line) {
        List<Integer> prioritizedTasks = new ArrayList<>();
        String[] parts = line.split(",");
        for (String part : parts) {
            int id = Integer.parseInt(part);
            prioritizedTasks.add(id);
        }
        return prioritizedTasks;
    }


//Шапка CSV файла
    String getHeader() {
        return "id,type,name,status,description,startDataTime,duration,epic";
    }

}

