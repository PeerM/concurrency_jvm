package de.hs_augsburg.nlp.three.radix;


import de.hs_augsburg.nlp.three.histogram.ClojureHelpers;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("Duplicates")
public class ThreeRadixSort implements ISort {

    private final ExecutorService pool;

    public ThreeRadixSort() {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public int[] sort(int[] a) {
        try {
            int RADIX = 8;
            int RADICES = 1 << RADIX;
            int mask = (1 << RADIX) - 1;
            for (int bits = 0; bits < 32; bits += RADIX) {
                // 1st step: Calculate histogram with RADICES entries (RADICES = 1<<RADIX)
                int[] histogram = histogram(a, RADICES, bits, mask);

                // 2nd step: Prescan the histogram bucket */
                stepTwo(RADICES, histogram);

                // 3rd step: Rearrange the elements based on prescaned histogram */
                ArrayList<Future<int[]>> futures = new ArrayList<>();
                for (int i = 31; i <= 255; i += 32) {
                    final int[] fa = a;
                    final int fi = i;
                    final int fbits = bits;
//                    pool.execute(() -> stepThree(fa, fbits, sortedData, histogram, mask, fi, latch));
                    futures.add(pool.submit(() -> stepThree(fa, fbits, histogram, mask, fi)));
                }
                a = ClojureHelpers.arrayElementBasedAdd(futures.get(0).get(), futures.get(1).get());

            }
            return a;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("something went wrong", e);
        }
    }

    private void stepTwo(int RADICES, int[] histogram) {
        int sum = 0;
        for (int i = 0; i < RADICES; ++i) {
            int val = histogram[i];
            histogram[i] = sum;
            sum += val;
        }
    }

    private int[] stepThree(int[] a, int bits, int[] histogram, int mask, int valueBorder) {
        int[] sortedData = new int[a.length];
        for (int i = 0; i < a.length; ++i) {
            int value = (a[i] >> bits) & mask;
            if (valueBorder - 32 < value && value <= valueBorder)
                try {
                    sortedData[histogram[value]++] = a[i];
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    throw e;
                }
        }
        return sortedData;
    }

    public static int[] histogram(int[] a, int RADICES, int bit, int mask) {
        int[] result = new int[RADICES];
        for (int i = 0; i < a.length; i++) {
            result[(a[i] >> bit) & mask]++;
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100);
        }
        int sum = 0;
        for (int i = 0; i < 100000; i++) {
            sum += random.nextInt();
        }
        ThreeRadixSort radixSort = new ThreeRadixSort();
        doWork(random, radixSort);
    }

    private static void doWork(Random random, ThreeRadixSort radixSort) {
        System.out.println("starting");
        for (int i = 0; i < 50; i++) {
            int[] ints = random.ints(10000000, 0, Integer.MAX_VALUE).toArray();
            int[] subject = radixSort.sort(ints);
        }
    }

    @Override
    public void close() {
        pool.shutdown();
    }


}
