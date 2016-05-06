package de.hs_augsburg.nlp.three.sort;

import de.hs_augsburg.nlp.three.radix.SequentialRadixSort;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static de.hs_augsburg.nlp.three.radix.SequentialRadixSort.histogram;

public class SequentialRadixSortTest {

    @Test
    public void histogram_test() throws Exception {
        int[] ints = {0b0000, 0b1000, 0b0001};
        Assert.assertArrayEquals(new int[]{2, 1, 0, 0}, histogram(ints, 1 << 2, 0));
        Assert.assertArrayEquals(new int[]{2, 0, 1, 0}, histogram(ints, 1 << 2, 2));
    }

    @Test
    public void sort_test() throws Exception {
        SequentialRadixSort sorter = new SequentialRadixSort();
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        int[] originals = ints.clone();
        ArrayUtils.shuffleArray(ints);
        Assert.assertArrayEquals(originals, sorter.sort(ints));
    }

    @Test
    public void regression_test() throws Exception {
        SequentialRadixSort sorter = new SequentialRadixSort();
        int[] ints = new Random().ints(200, 0, Integer.MAX_VALUE).toArray();
        int[] subject = sorter.sort(ints);
        Arrays.sort(ints);
        Assert.assertArrayEquals(ints, subject);
    }
}