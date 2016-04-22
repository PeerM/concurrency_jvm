package de.hs_augsburg.nlp.two;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


public class VectorQueue<T> extends AbstractQueue<T> {
    private final int maxElements = 50;
    private final List<T> vec = new Vector<>(maxElements);
    private volatile int end = 0;
    private volatile int start = 0;
    private volatile boolean full = false;

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public int size() {
        int size = 0;

        if (end < start) {
            size = maxElements - start + end;
        } else if (end == start) {
            size = full ? maxElements : 0;
        } else {
            size = end - start;
        }

        return size;
    }

    @Override
    public boolean offer(T t) {
        if (size() == maxElements)
            return false;
        vec.add(end++, t);

        if (end >= maxElements) {
            end = 0;
        }

        if (end == start) {
            full = true;
        }

        return true;

    }

    @Override
    public T poll() {
        if (isEmpty()) {
            return null;
        }
        final T element = vec.get(start);
        if (null != element) {
            vec.add(start++, null);

            if (start >= maxElements) {
                start = 0;
            }
            full = false;
        }
        return element;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return vec.get(start);
    }
}
