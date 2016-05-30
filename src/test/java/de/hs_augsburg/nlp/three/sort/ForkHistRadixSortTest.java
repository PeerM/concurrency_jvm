package de.hs_augsburg.nlp.three.sort;

import de.hs_augsburg.nlp.three.radix.ForkHistRadixSort;
import de.hs_augsburg.nlp.three.radix.FutureHistRadixSort;
import de.hs_augsburg.nlp.three.radix.ISort;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

@SuppressWarnings("Duplicates")
@Ignore
public class ForkHistRadixSortTest {

    @Test
    public void sort_test() throws Exception {
        ISort sorter = new ForkHistRadixSort();
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        int[] originals = ints.clone();
        ArrayUtils.shuffleArray(ints);
        int[] sorted = sorter.sort(ints);
        Assert.assertArrayEquals(originals, sorted);
        sorter.close();
    }

    @Test
    public void regression_test() throws Exception {
        ISort sorter = new ForkHistRadixSort();
        int[] ints = new Random().ints(200, 0, Integer.MAX_VALUE).toArray();
        int[] subject = sorter.sort(ints);
        Arrays.sort(ints);
        Assert.assertArrayEquals(ints, subject);
        sorter.close();
    }
}