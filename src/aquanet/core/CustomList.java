package aquanet.core;

/**
 * Custom dynamic array implementing a self-resizing list.
 * Satisfies the strict zero-java.util engine constraint.
 */
public class CustomList<T> {
    private Object[] data;
    private int size;
    private static final int INITIAL_CAPACITY = 8;

    public CustomList() {
        this.data = new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    public void add(T element) {
        if (size == data.length) {
            resize(data.length * 2);
        }
        data[size++] = element;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) data[index];
    }

    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        data[index] = element;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = null;
        if (size > 0 && size <= data.length / 4 && data.length > INITIAL_CAPACITY) {
            resize(data.length / 2);
        }
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }
}