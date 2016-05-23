package de.hs_augsburg.nlp.three.sort;

import de.hs_augsburg.nlp.three.radix.ISort;
import de.hs_augsburg.nlp.three.radix.SequentialRadixSort;
import de.hs_augsburg.nlp.three.radix.ThreeRadixSort;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static de.hs_augsburg.nlp.three.radix.SequentialRadixSort.histogram;

@SuppressWarnings("Duplicates")
public class ThreeRadixSortTest {

    @Test
    public void sort_test() throws Exception {
        ISort sorter = new ThreeRadixSort();
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,0xF00,0x0F0000};
        int[] originals = ints.clone();
        ArrayUtils.shuffleArray(ints);
        Assert.assertArrayEquals(originals, sorter.sort(ints));
    }

    @Test
    public void regression_test() throws Exception {
        ISort sorter = new ThreeRadixSort();
        int[] ints = new Random().ints(2000, 0, Integer.MAX_VALUE).toArray();
        int[] subject = sorter.sort(ints);
        Arrays.sort(ints);
        Assert.assertArrayEquals(ints, subject);
    }
}