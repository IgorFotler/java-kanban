package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> history = new HashMap<>();

    private static class Node<T> {
        Node<Task> previous;
        Task task;
        Node<Task> next;

        public Node(Node<Task> previous, Task task, Node<Task> next) {
            this.previous = previous;
            this.task = task;
            this.next = next;
        }
    }

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId())) {
                remove(task.getId());
            }
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public List<Task> returnHistory() {
        return tasksInHistory();
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            if (node.previous != null) {
                node.previous.next = node.next;
            } else {
                head = node.next;
                if (head != null) {
                    head.previous = null;
                }
            }
            if (node.next != null) {
                node.next.previous = node.previous;
            } else {
                tail = node.previous;
                if (tail != null) {
                    tail.next = null;
                }
            }
        }
    }

    public List<Task> tasksInHistory() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    public void clear() {
        history.clear();
        head = null;
        tail = null;
    }
}
