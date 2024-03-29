package de.hs_augsburg.nlp.two.reduced;


import com.google.common.collect.Iterators;

import java.util.*;

public class MultipleBackedList<T> extends AbstractList<T> {
    private List<List<T>> backing;
    private List<Integer> sizes;
    private int completeSize;

    public MultipleBackedList() {
        backing = new LinkedList<>();
        sizes = new LinkedList<>();
        completeSize = 0;
    }

    @Override
    public T get(int index) {
        int accumulatedSize = 0;
        int backingIndex = 0;
        for (int singleSize : sizes) {
            if (singleSize + accumulatedSize >= index)
                return backing.get(backingIndex).get(index - accumulatedSize);
            else {
                backingIndex++;
                accumulatedSize += singleSize;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<Iterator<T>> metaIter = backing.stream().map(List::iterator).iterator();
        return Iterators.concat(metaIter);
    }

    private void addList(List<T> toAdd) {
        backing.add(toAdd);
        sizes.add(toAdd.size());
        completeSize += toAdd.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c instanceof List) {
            // it's a instance of a List and is of type T
            addList((List<T>) c);
            return true;
        }
        throw new IllegalArgumentException(c.getClass().getSimpleName()+ " is not an Instance of List");
//        return super.addAll(c);
    }

    @Override
    public int size() {
        return completeSize;
    }
}
