package de.hs_augsburg.nlp.three.radix;


import java.util.Arrays;

public class JdkSort implements ISort {
    @Override
    public int[] sort(int[] a) {
        int[] sortedData = a.clone();
        Arrays.sort(sortedData);
        return sortedData;
    }
}
