package de.hs_augsburg.nlp.two;

import java.util.*;


public class VectorQueue<T> extends AbstractQueue<T> {
    private final int maxElements = 50;
    private final List<T> vec = new Vector<>(maxElements);
    private volatile int end = 0;
    private volatile int start = 0;
    private volatile boolean full = false;


    public VectorQueue() {
        for (int i = 0; i < maxElements; i++) {
            vec.add(null);
        }
    }

    @Override
    public synchronized int size() {
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
    public synchronized boolean offer(T t) {
        if (size() == maxElements)
            return false;
        vec.set(end++, t);

        if (end >= maxElements) {
            end = 0;
        }

        if (end == start) {
            full = true;
        }

        return true;

    }

    @Override
    public synchronized T poll() {
        if (isEmpty()) {
            return null;
        }
        final T element = vec.get(start);
        if (null != element) {
            vec.set(start++, null);

            if (start >= maxElements) {
                start = 0;
            }
            full = false;
        }
        return element;
    }

    @Override
    public synchronized T peek() {
        if (isEmpty()) {
            return null;
        }
        return vec.get(start);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private volatile int index = start;
            private volatile int lastReturnedIndex = -1;
            private volatile boolean isFirst = full;

            @Override
            public boolean hasNext() {
                return isFirst || index != end;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                isFirst = false;
                lastReturnedIndex = index;
                index = increment(index);
                return vec.get(lastReturnedIndex);
            }
        };
    }

    private int increment(int index) {
        index++;
        if (index >= maxElements) {
            index = 0;
        }
        return index;
    }


}
