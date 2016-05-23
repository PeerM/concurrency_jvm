package de.hs_augsburg.nlp.three.radix;

public interface ISort extends AutoCloseable{
    int[] sort(int[] a);

    @Override
    default void close() {

    }
}
