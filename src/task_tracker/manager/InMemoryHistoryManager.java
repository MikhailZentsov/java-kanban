package task_tracker.manager;

import task_tracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> history;

    InMemoryHistoryManager() {
        this.history = new CustomLinkedList<>();
    }

    @Override
    public void add(Task task) {
        history.add(task, task.getId());
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    public void clear() { history.clear(); }
    private static class CustomLinkedList<T> {
        transient int size = 0;
        transient CustomLinkedList.Node<T> first;
        transient CustomLinkedList.Node<T> last;
        private final transient Map<Integer, CustomLinkedList.Node<T>> map;

        CustomLinkedList() {
            this.map = new HashMap<>();
        }

        CustomLinkedList.Node<T> linkLast(T element) {
            final CustomLinkedList.Node<T> l = last;
            final CustomLinkedList.Node<T> newNode = new CustomLinkedList.Node<>(l, element, null);
            last = newNode;

            if (l == null) {
                first = newNode;
            } else {
                l.next = newNode;
            }

            size++;

            return newNode;
        }

        boolean add(T element, int key) {
            try {
                if (map.containsKey(key)) {
                    unlink(map.get(key));
                }

                map.put(key, linkLast(element));

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        T unlink(CustomLinkedList.Node<T> node) {
            final T element = node.item;
            final CustomLinkedList.Node<T> next = node.next;
            final CustomLinkedList.Node<T> prev = node.prev;

            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }

            node.item = null;
            size--;

            return element;
        }

        boolean remove(int id) {
            if (map.containsKey(id)) {
                CustomLinkedList.Node<T> node = map.get(id);

                unlink(node);
                map.remove(id);

                return true;
            }

            return false;
        }

        List<T> getHistory() {
            List<T> list = new ArrayList<>();

            if (first != null) {
                CustomLinkedList.Node<T> node = first;

                do {
                    list.add(node.item);
                    node = node.next;
                } while (node != null);
            }

            return list;
        }

        int size() {
            return size;
        }

        void clear() {
            for (Node<T> x = first; x != null; ) {
                Node<T> next = x.next;
                x.item = null;
                x.next = null;
                x.prev = null;
                x = next;
            }
            first = last = null;
            size = 0;
        }

        private static class Node<T> {
            T item;
            Node<T> next;
            Node<T> prev;

            Node(Node<T> prev, T element, Node<T> next) {
                this.item = element;
                this.next = next;
                this.prev = prev;
            }
        }
    }
}