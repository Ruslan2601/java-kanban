package main.services;

import main.interfaces.HistoryManager;
import main.models.Task;
import main.util.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> history = new CustomLinkedList<>();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    //добавляем задачу в историю просмотров
    @Override
    public void addHistory(Task task) {
        if (historyMap.containsKey(task.getId())) {
            history.removeNode(historyMap.get(task.getId()));
        }
        historyMap.put(task.getId(), history.linkLast(task));
    }

    //удаляем задачу из истории
    @Override
    public void remove(int id) {
        history.removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    //получаем историю просмотров
    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    static class CustomLinkedList<T> {

        private Node<Task> head;
        private Node<Task> tail;

        public Node<Task> linkLast(Task element) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<Task>(tail, element, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            return newNode;
        }

        public List<Task> getTasks() {
            List<Task> tasks = new LinkedList<>();
            Node<Task> currentNode = head;
            while (currentNode != null) {
                tasks.add(currentNode.data);
                currentNode = currentNode.next;
            }
            return tasks;
        }

        public void removeNode(Node node) {
            if (node != null) {
                final Node next = node.next;
                final Node previous = node.prev;
                node.data = null;

                if (head == node && tail == node) {
                    head = null;
                    tail = null;
                } else if (head == node && tail != node) {
                    head = next;
                    head.prev = null;
                } else if (head != node && tail == node) {
                    tail = previous;
                    tail.next = null;
                } else {
                    previous.next = next;
                    next.prev = previous;
                }
            }
        }
    }
}
