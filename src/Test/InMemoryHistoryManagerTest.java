package Test;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.service.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    //Получение истории пустой просмотров
    @Test
    public void shouldReturnListHistoryEmpty() {
        assertTrue(inMemoryTaskManager.getHistory().isEmpty());
    }

    //Проверка истории просмотров на дубли
    @Test
    public void checkListHistoryforDuplication() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        inMemoryTaskManager.saveEpic(epic);
        inMemoryTaskManager.getEpic(1);
        inMemoryTaskManager.getEpic(1);
        inMemoryTaskManager.getEpic(1);

        Assertions.assertEquals(inMemoryTaskManager.getHistory().size(), 1);
    }

    //Проверка истории просмотров на удаление начала
    @Test
    public void checkListHistoryforRemoveHead() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");
        Epic epic3 = new Epic("Эпик №3", "Описание эпика №3");
        inMemoryTaskManager.saveEpic(epic);
        inMemoryTaskManager.saveEpic(epic2);
        inMemoryTaskManager.saveEpic(epic3);
        inMemoryTaskManager.getEpic(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.removeEpic(1);

        Epic epicTest = new Epic(
                "Эпик №2", "Описание эпика №2", TaskStatus.NEW,2, epic2.getStartDateTime(),0);
        Epic epicTest2 = new Epic(
                "Эпик №3", "Описание эпика №3", TaskStatus.NEW,3, epic3.getStartDateTime(),0);
        List<Epic> epicsTest = new ArrayList<>();
        epicsTest.add(epicTest);
        epicsTest.add(epicTest2);

        String testEpics = epicsTest.toString();
        String Epics = inMemoryTaskManager.getHistory().toString();
        Assertions.assertEquals(Epics, testEpics);
    }

    //Проверка истории просмотров на удаление Середины
    @Test
    public void checkListHistoryforRemoveBody() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");
        Epic epic3 = new Epic("Эпик №3", "Описание эпика №3");
        inMemoryTaskManager.saveEpic(epic);
        inMemoryTaskManager.saveEpic(epic2);
        inMemoryTaskManager.saveEpic(epic3);
        inMemoryTaskManager.getEpic(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.removeEpic(2);

        Epic epicTest = new Epic(
                "Эпик №1", "Описание эпика №1", TaskStatus.NEW,1, epic.getStartDateTime(),0);
        Epic epicTest2 = new Epic(
                "Эпик №3", "Описание эпика №3", TaskStatus.NEW,3, epic3.getStartDateTime(),0);
        List<Epic> epicsTest = new ArrayList<>();
        epicsTest.add(epicTest);
        epicsTest.add(epicTest2);

        String testEpics = epicsTest.toString();
        String Epics = inMemoryTaskManager.getHistory().toString();
        Assertions.assertEquals(Epics, testEpics);
    }

    //Проверка истории просмотров на удаление конца
    @Test
    public void checkListHistoryforRemoveTail() {
        Epic epic = new Epic("Эпик №1", "Описание эпика №1");
        Epic epic2 = new Epic("Эпик №2", "Описание эпика №2");
        Epic epic3 = new Epic("Эпик №3", "Описание эпика №3");
        inMemoryTaskManager.saveEpic(epic);
        inMemoryTaskManager.saveEpic(epic2);
        inMemoryTaskManager.saveEpic(epic3);
        inMemoryTaskManager.getEpic(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.removeEpic(3);

        Epic epicTest = new Epic(
                "Эпик №1", "Описание эпика №1", TaskStatus.NEW,1, epic.getStartDateTime(),0);
        Epic epicTest2 = new Epic(
                "Эпик №2", "Описание эпика №2", TaskStatus.NEW,2, epic2.getStartDateTime(),0);
        List<Epic> epicsTest = new ArrayList<>();
        epicsTest.add(epicTest);
        epicsTest.add(epicTest2);

        String testEpics = epicsTest.toString();
        String Epics = inMemoryTaskManager.getHistory().toString();
        Assertions.assertEquals(Epics, testEpics);
    }

}
