package Test;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest <T extends TaskManager>{

    TaskManager taskManager = Managers.getDefault();

    //Получение задач по приоритетности
    @Test
    public void shouldReturnTaskPrioritet () throws InterruptedException {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        Task task2 = new Task("Задача №2", "Описание задачи №2");
        taskManager.saveTask(task2);
        Task newTask = new Task(
                "Задача №1",
                "Описание задачи №1",1, taskManager.getTask(1).getStartDateTime(),1);
        Task newTask2 = new Task(
                "Задача №2",
                "Описание задачи №2",2, taskManager.getTask(2).getStartDateTime(),1);
        Comparator<Task> taskComparatorTest = Comparator.comparing(Task::getStartDateTime);
        Set<Task> prioritizedTasks = new TreeSet<>(taskComparatorTest);
        prioritizedTasks.add(newTask);
        prioritizedTasks.add(newTask2);
        taskManager.getPrioritizedTasks();
        Assertions.assertEquals(taskManager.getPrioritizedTasks(),prioritizedTasks);

        task = new Task("Задача №1",
                "Описание задачи №1",1,
                LocalDateTime.now().plusDays(5),30);
        taskManager.updateTask(task);
        prioritizedTasks.clear();
        newTask = new Task(
                "Задача №1",
                "Описание задачи №1",1, taskManager.getTask(1).getStartDateTime(),30);
        prioritizedTasks.add(newTask2);
        prioritizedTasks.add(newTask);
        Assertions.assertEquals(taskManager.getPrioritizedTasks(),prioritizedTasks);
    }

    //Проверка сохранения истории просмотров
    @Test
    public void shouldReturnTaskInHistoryManager () {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        taskManager.getTask(1);
        Task newTask = new Task(
                "Задача №1",
                "Описание задачи №1",1, taskManager.getTask(1).getStartDateTime(),1);
        Assertions.assertEquals(taskManager.getHistory().get(0),newTask);
    }

    //Проверка создания и запись Задачи в список задач
    @Test
    public void creatingTaskShouldAppearInTheTaskList() {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        Task newTask = new Task(
                "Задача №1",
                "Описание задачи №1",1, taskManager.getTask(1).getStartDateTime(),1);
        Assertions.assertEquals(taskManager.getTask(1),newTask);
    }

    //Проверка создания и запись Подзадачи в список подзадач
    @Test
    public void creatingSubTaskShouldAppearInTheSubTaskList() {
        Epic epic1 = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic1); //Создание Епика
        SubTask subTask1 = new SubTask("Подзадача №1", "Описание Подзадачи №1", 1);
        taskManager.saveSubTask(subTask1);
        SubTask currentSubTask
                = new SubTask(
                        "Подзадача №1",
                "Описание Подзадачи №1",
                TaskStatus.NEW,2, taskManager.getSubTask(2).getStartDateTime(),1,1);
        Assertions.assertEquals(taskManager.getSubTask(2),currentSubTask);
    }

    //Проверка создания и запись Эпика в список Эпиков
    @Test
    public void creatingEpicShouldAppearInTheEpicList() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        Epic currentEpic = new Epic("Эпик №1", "Описание Эпика №1",1, taskManager.getEpic(1).getStartDateTime(),1);
        Assertions.assertEquals(taskManager.getEpic(1),currentEpic);
    }

    //Проверка смнены статуса Задачи
    @Test
    public void chekUpdateStatusTask() {
        Task task = new Task("Задача №1" , "Описание задачи №1");
        taskManager.saveTask(task);
        task = new Task(
                "Задача №1",
                "Описание задачи №1",
                TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.now(),1);
        taskManager.updateTask(task);
        Assertions.assertEquals(taskManager.getTask(1).getStatus(),TaskStatus.IN_PROGRESS);
    }

    //Проверка создание подзадачи без эпика
    @Test
    public void chekSaveSubtaskNotEpic() {
        SubTask subTask = new SubTask("Подзадача №1", "Описание подзадачи №1",1);
        Assertions.assertEquals(taskManager.saveSubTask(subTask), null);

    }

    //Проверка смены статуса подзадачи
    @Test
    public void chekUpdateStatusSubtask() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание подзадачи №1",1);
        taskManager.saveSubTask(subTask);
        Assertions.assertEquals(taskManager.getSubTask(2).getStatus(), TaskStatus.NEW);
        subTask = new SubTask(
                "Подзадача №1",
                "Описание подзадачи №1",
                TaskStatus.IN_PROGRESS,2, LocalDateTime.now(),1,1);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(taskManager.getSubTask(2).getStatus(), TaskStatus.IN_PROGRESS);
    }

    //Проверка созданного статуса Епика NEW без подзадач
    @Test
    public void chekStatusEpicNewNoSubtask() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        Assertions.assertEquals(taskManager.getEpic(1).getStatus(), TaskStatus.NEW);
    }

    //Проверка созданного статуса Епика NEW c подзадачей
    @Test
    public void chekStatusEpicNewToSubtask() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        SubTask subTask = new SubTask("Подзадача №1", "Описание подзадачи №1", 1);
        taskManager.saveEpic(epic);
        taskManager.saveSubTask(subTask);
        Assertions.assertEquals(taskManager.getEpic(1).getStatus(), TaskStatus.NEW);
    }

    //Проверка Эпика смнены статуса на прогресс при двух подзадачах одна из которых прогресс
    @Test
    public void chekStatusEpicInprogressToSubtaskUpdateStatusInprogress() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание подзадачи №1",1);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание подзадачи №2",1);
        taskManager.saveSubTask(subTask);
        taskManager.saveSubTask(subTask2);
        subTask = new SubTask(
                "Подзадача №1",
                "Описание подзадачи №1", TaskStatus.IN_PROGRESS,2, LocalDateTime.now(),1,1);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(taskManager.getEpic(1).getStatus(), TaskStatus.IN_PROGRESS);
    }

    //Проверка Эпика смнены статуса на Выполнен при подзадаче Выполен
    @Test
    public void chekStatusEpicDoneToSubtaskUpdateStatusdone() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание подзадачи №1",1);
        taskManager.saveSubTask(subTask);
        subTask = new SubTask(
                "Подзадача №1",
                "Описание подзадачи №1", TaskStatus.DONE,2, LocalDateTime.now(),1,1);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(taskManager.getEpic(1).getStatus(), TaskStatus.DONE);
    }

    //Проверка Эпика смнены статуса на прогресс при двух подзадачах одна из которых прогресс
    @Test
    public void chekStatusEpicInprogressToSubtaskUpdateStatusDoneAndNew() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание подзадачи №1",1);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание подзадачи №2",1);
        taskManager.saveSubTask(subTask);
        taskManager.saveSubTask(subTask2);
        subTask = new SubTask(
                "Подзадача №1",
                "Описание подзадачи №1",
                TaskStatus.DONE,2, LocalDateTime.now(),1,1);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(taskManager.getEpic(1).getStatus(), TaskStatus.IN_PROGRESS);
    }

    //Проверка удаления задач
    @Test
    public void checkRemoveTask() {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        taskManager.removeTask(1);
        Assertions.assertEquals(taskManager.getTask(1), null);
    }

    //Проверка удаления Подзадачи
    @Test
    public void checkRemoveSubTask() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание Подзадача №1",1);
        taskManager.saveSubTask(subTask);
        taskManager.removeSubTask(2);
        Assertions.assertEquals(taskManager.getSubTask(2), null);
    }

    //Проверка удаления Епика
    @Test
    public void checkRemoveEpic() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        taskManager.removeEpic(1);
        Assertions.assertEquals(taskManager.getEpic(1), null);
    }

    //Проверка удаления задач
    @Test
    public void checkRemoveTaskNonExistent() {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        taskManager.removeTask(1000);
        Assertions.assertEquals(taskManager.getTask(1000), null);
    }

    //Проверка удаления Подзадачи
    @Test
    public void checkRemoveSubTaskNonExistent() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание Подзадача №1",1);
        taskManager.saveSubTask(subTask);
        taskManager.removeSubTask(2);
        Assertions.assertEquals(taskManager.getSubTask(2), null);
    }

    //Проверка удаления Епика
    @Test
    public void checkRemoveEpicNonExistent() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        taskManager.removeEpic(1);
        Assertions.assertEquals(taskManager.getEpic(1), null);
    }

    //Проверка удаления всех задач
    @Test
    public void checkRemoveAllTask() {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        Task task2 = new Task("Задача №2", "Описание задачи №2");
        taskManager.saveTask(task2);
        taskManager.removeAllTask();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    //Проверка удаления всех задач
    @Test
    public void checkRemoveAllSubTask() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание Подзадача №1",1);
        taskManager.saveSubTask(subTask);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание Подзадача №2",1);
        taskManager.saveSubTask(subTask2);
        taskManager.removeAllSubTask();
        assertTrue(taskManager.getAllSubTask().isEmpty());
    }

    //Проверка удаления всех епиков
    @Test
    public void checkRemoveAllEpic() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");
        taskManager.saveEpic(epic2);
        taskManager.removeAllEpic();
        assertTrue(taskManager.getAllEpic().isEmpty());
    }

    //Проверка получения задачи
    @Test
    public void checkGetTask() {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        Task currentTask = new Task(
                "Задача №1",
                "Описание задачи №1",TaskStatus.NEW,1, taskManager.getTask(1).getStartDateTime(),1);
        Assertions.assertEquals(taskManager.getTask(1), currentTask);
    }

    //Проверка получения епика
    @Test
    public void checkGetEpic() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        Epic currentEpic = new Epic(
                "Эпик №1", "Описание Эпика №1",TaskStatus.NEW,1, taskManager.getEpic(1).getStartDateTime(),1);
        Assertions.assertEquals(taskManager.getEpic(1), currentEpic);
    }

    //Проверка получения Подзадачи
    @Test
    public void checkGetSubtask() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание Подзадачи №1",1);
        taskManager.saveSubTask(subTask);
        SubTask currentSubtask
                = new SubTask(
                        "Подзадача №1",
                "Описание Подзадачи №1",
                TaskStatus.NEW,2,  taskManager.getSubTask(2).getStartDateTime(),1,1);
        Assertions.assertEquals(taskManager.getSubTask(2), currentSubtask);
    }

    //Проверка получение несуществующей задачи
    @Test
    public void checkGetTaskNonExistent() {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        Assertions.assertEquals(taskManager.getTask(1000), null);
    }

    //Проверка получение несуществующего епика
    @Test
    public void checkGetEpicNonExistent() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        Assertions.assertEquals(taskManager.getEpic(1000), null);
    }

    //Проверка получение несуществующей Подзадачи
    @Test
    public void checkGetSubtaskNonExistent() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание Подзадачи №1",1);
        taskManager.saveSubTask(subTask);
        Assertions.assertEquals(taskManager.getSubTask(1000), null);
    }

    //Получение списка всех задач
    @Test
    public void shouldReturnListTasks() {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        Task task2 = new Task("Задача №2", "Описание задачи №2");
        taskManager.saveTask(task2);
        Task currentTask = new Task(
                "Задача №1",
                "Описание задачи №1",1, taskManager.getTask(1).getStartDateTime(),1);
        Task currentTask2 = new Task(
                "Задача №2",
                "Описание задачи №2",2,  taskManager.getTask(2).getStartDateTime(),1);
        List<Task> collectionTask = new ArrayList<>();
        collectionTask.add(currentTask);
        collectionTask.add(currentTask2);
        Assertions.assertEquals(taskManager.getAllTasks(), collectionTask);
    }

    //Получение списка всех подзадач
    @Test
    public void shouldReturnListSubTask() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание Подзадачи №1",1);
        taskManager.saveSubTask(subTask);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание Подзадача №2",1);
        taskManager.saveSubTask(subTask2);

        SubTask currentSubTask
                = new SubTask(
                        "Подзадача №1",
                "Описание Подзадачи №1",
                TaskStatus.NEW,2,  taskManager.getSubTask(2).getStartDateTime(),1,1);
        SubTask currentSubtask2
                = new SubTask(
                        "Подзадача №2",
                "Описание Подзадача №2",
                TaskStatus.NEW,3, taskManager.getSubTask(3).getStartDateTime(),1,1);
        List<Task> collectionSubTask = new ArrayList<>();
        collectionSubTask.add(currentSubTask);
        collectionSubTask.add(currentSubtask2);
        Assertions.assertEquals(taskManager.getAllSubTask(), collectionSubTask);
    }

    //Получение списка всех епиков
    @Test
    public void shouldReturnListEpic() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.saveEpic(epic);
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");
        taskManager.saveEpic(epic2);
        Epic currentEpic = new Epic("Эпик №1",
                "Описание эпика №1",1, taskManager.getEpic(1).getStartDateTime(),1);
        Epic currentEpic2 = new Epic("Эпик №2",
                "Описание эпика №2",2, taskManager.getEpic(2).getStartDateTime(),1);
        List<Task> collectionEpic = new ArrayList<>();
        collectionEpic.add(currentEpic);
        collectionEpic.add(currentEpic2);
        Assertions.assertEquals(taskManager.getAllEpic(), collectionEpic);
    }

    //Получение списка всех подзадач Определенного епика
    @Test
    public void shouldReturnListSubTaskByEpic() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание Подзадачи №1", 1);
        taskManager.saveSubTask(subTask);
        SubTask subTask2 = new SubTask("Подзадача №2", "Описание Подзадача №2", 1);
        taskManager.saveSubTask(subTask2);
        SubTask currentSubTask
                = new SubTask("Подзадача №1",
                "Описание Подзадачи №1",
                TaskStatus.NEW, 2, taskManager.getSubTask(2).getStartDateTime(),1,1);
        SubTask currentSubtask2
                = new SubTask("Подзадача №2",
                "Описание Подзадача №2",
                TaskStatus.NEW, 3, taskManager.getSubTask(3).getStartDateTime(),1,1);
        List<Task> collectionSubTask = new ArrayList<>();
        collectionSubTask.add(currentSubTask);
        collectionSubTask.add(currentSubtask2);
        Assertions.assertEquals(taskManager.getSubtaskByEpik(1), collectionSubTask);
    }

    //Обновление Задачи
    @Test
    public void updateTask() {
        Task task = new Task("Задача №1", "Описание задачи №1");
        taskManager.saveTask(task);
        task = new Task(
                "Задача №1 Обновление",
                "Описание задачи №1 Обновление",1, taskManager.getTask(1).getStartDateTime(),1);
        taskManager.updateTask(task);
        Task currentTask = new Task(
                "Задача №1 Обновление",
                "Описание задачи №1 Обновление",1, taskManager.getTask(1).getStartDateTime(),1);

        Assertions.assertEquals(taskManager.getTask(1), currentTask);
    }

    //Обновление Подзадачи
    @Test
    public void updateSubTask() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", "Описание Подзадачи №1", 1);
        taskManager.saveSubTask(subTask);
        subTask = new SubTask(
                "Подзадача №1 Обновление",
                "Описание Подзадачи №1 Обновление",
                 TaskStatus.NEW, 2, taskManager.getSubTask(2).getStartDateTime(),1,1
        );
        taskManager.updateSubTask(subTask);
        SubTask currentsubTask = new SubTask(
                "Подзадача №1 Обновление",
                "Описание Подзадачи №1 Обновление",
                TaskStatus.NEW, 2, taskManager.getSubTask(2).getStartDateTime(),1,1
        );

        Assertions.assertEquals(taskManager.getSubTask(2), currentsubTask);
    }

    //Обновление Епика
    @Test
    public void updateEpic() {
        Epic epic = new Epic("Эпик №1", "Описание Эпика №1");
        taskManager.saveEpic(epic);
        epic = new Epic(
                "Эпик №1 Обновление",
                "Описание Эпика №1 Обновление",1,
                taskManager.getEpic(1).getStartDateTime(),1);
        taskManager.updateEpic(epic);
        Epic currentEpic = new Epic(
                "Эпик №1 Обновление",
                "Описание Эпика №1 Обновление",1,
                taskManager.getEpic(1).getStartDateTime(),1);

        Assertions.assertEquals(taskManager.getEpic(1), currentEpic);
    }
}
