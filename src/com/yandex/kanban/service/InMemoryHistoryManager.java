package com.yandex.kanban.service;

import com.yandex.kanban.model.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> saveNode = new HashMap<>();
    private  Node head;
    private  Node tail;


    //Добавление с содержанием пред задачи тоесть в иметированный хвост
    private void saveLast(Task task) {
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
    public LinkedList<Task> getHistory() {
        LinkedList<Task> tasks = new LinkedList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.value);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public void remove(int id) {
        Node node = saveNode.remove(id);
        if (node != null) {

            Node prev = node.prev;
            Node next = node.next;

            if (head == node) {
                head = node.next;
            }
            if (tail == node) {
                tail = node.prev;
            }

            if (prev != null) {
                prev.next = next;
            }

            if (next != null) {
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
    //Связный список
    private static class Node {
        public Task value;
        public Node next;
        public Node prev;

        public Node(Node prev, Task value, Node next) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

}


