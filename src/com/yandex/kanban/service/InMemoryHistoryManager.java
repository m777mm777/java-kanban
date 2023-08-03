package com.yandex.kanban.service;

import com.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> saveNode = new HashMap<>();
    public Node head;
    public Node tail;

    //Добавление с содержанием пред задачи тоесть в иметированный хвост
    public void saveLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        saveNode.put(task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        tail = newNode;
    }

   //Получение списка всех просмотров
    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.value);
            current = current.next;
        }
        return tasks;
    }

    //Удалить задачу из просмотра если она была ранее
    @Override
    public void remove(int id) {
        Node node = saveNode.get(id);
        if (node != null) {
            final Node next = node.next;
            final Node prev = node.prev;
            node.value = null;
            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node) {
                head = next;
                head.prev = null;
            } else if (tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }

    //Добавление и удаление если уже было
    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            saveLast(task);
        }
    }

}

//Связный список
class Node {
    public Task value;
    public Node next;
    public Node prev;

    public Node(Node prev, Task value, Node next) {
        this.value = value;
        this.next = next;
        this.prev = prev;
    }
}
