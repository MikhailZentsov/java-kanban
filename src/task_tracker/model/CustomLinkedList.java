package task_tracker.model;

import java.util.*;

public class CustomLinkedList<T> {
    transient int size = 0;
    transient CustomLinkedList.Node<T> first;
    transient CustomLinkedList.Node<T> last;
    private final Map<Integer, Node<T>> map;

    public CustomLinkedList() {
        this.map = new HashMap<>();
    }

    Node<T> linkLast(T t) {
        final CustomLinkedList.Node<T> l = last;
        final CustomLinkedList.Node<T> newNode = new CustomLinkedList.Node<>(l, t, null);
        last = newNode;

        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }

        size++;

        return newNode;
    }

    public boolean add(T t) {
        try {
            var key = ((Task) t).getId();

            if (!map.containsKey(key)) {
                map.put(key, linkLast(t));

                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    T unlink(CustomLinkedList.Node<T> x) {
        final T element = x.item;
        final CustomLinkedList.Node<T> next = x.next;
        final CustomLinkedList.Node<T> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;

        return element;
    }

    public boolean remove(int id) {
        if (map.containsKey(id)) {
            Node<T> node = map.get(id);

            unlink(node);
            map.remove(id);

            return true;
        }

        return false;
    }

    public List<T> getHistory() {
        List<T> list = new ArrayList<>();

        if (first != null) {
            Node<T> node = first;

            do {
                list.add(node.item);
                node = node.next;
            } while (node != null);
        }

        return list;
    }

    public int size() {
        return size;
    }

    private static class Node<T> {
        T item;
        CustomLinkedList.Node<T> next;
        CustomLinkedList.Node<T> prev;

        Node(CustomLinkedList.Node<T> prev, T element, CustomLinkedList.Node<T> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}
