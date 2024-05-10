package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static final Map<Integer, Node<Task>> watchHistory = new HashMap<>();

    private Node<Task> first;
    private Node<Task> last;

    @Override
    public void addHistory(Task task) {
        System.out.println("Таск" + task.toString());
        System.out.println("add his call. size: " + getHistory());
        if (task != null) {
            System.out.println("Таск ID " + task.getId());
            String s = watchHistory.containsKey(task.getId()) ? watchHistory.get(task.getId()).toString() : "null";
            System.out.println(s);
            remove(task.getId());
            System.out.println("remove old node. size: " + getHistory());
            linkLast(task);
            System.out.println("write new node. size: " + getHistory());
            watchHistory.put(task.getId(), last);
            System.out.println("add to hash map. size: " + getHistory());
        }
        System.out.println("add his call end. size: " + getHistory());
    }

    @Override
    public void remove(int id) {
        removeNode(watchHistory.get(id));
        watchHistory.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node<Task> lastTask = last; // null
        Node<Task> buffer = new Node<>(lastTask, task, null);
        last = buffer;
        if (lastTask == null) {
            first = buffer;
        } else {
            lastTask.next = buffer;
        }

    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            if (node.prev == null && node.next == null) {
                last = null;
                first = null;
            } else {
                if (node.prev != null) {
                    node.prev.next = node.next;
                } else {
                    node.next.prev = null;
                    first = node.next;
                }
                if (node.next != null) {
                    node.next.prev = node.prev;
                } else {
                    node.prev.next = null;
                    last = node.prev;
                }
            }
        }
    }

    private List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> node = first;
        while (node != null) {
            tasks.add(node.item);
            node = node.next;
        }
        return tasks;
    }

    private static class Node<T extends Task> {
        T item;
        Node<T> next;
        Node<T> prev;

        Node(Node<T> prev, T item, Node<T> next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }
}



