package aquanet.core;

/**
 * Singly-linked FIFO Queue for Breadth-First Search (Edmonds-Karp / Dinic's level graphs).
 */
public class CustomQueue<T> {
    private static class Node<T> {
        T value;
        Node<T> next;
        Node(T val) { this.value = val; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int count;

    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        count++;
    }

    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T val = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        count--;
        return val;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return count;
    }
}