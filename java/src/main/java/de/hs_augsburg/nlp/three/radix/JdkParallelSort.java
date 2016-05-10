package de.hs_augsburg.nlp.three.radix;


import java.util.Arrays;

public class JdkParallelSort implements ISort {
    @Override
    public int[] sort(int[] a) {
        int[] sortedArray = a.clone();
        Arrays.parallelSort(sortedArray);
        return sortedArray;
    }
}
