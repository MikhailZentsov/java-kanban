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

    Node<T> linkLast(T element) {
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

    public boolean add(T element) {
        try {
            var key = ((Task) element).getId();

            if (!map.containsKey(key)) {
                map.put(key, linkLast(element));

                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
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
