package de.hs_augsburg.nlp.three.radix;


import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("Duplicates")
public class FutureHistRadixSort implements ISort {

    private ExecutorService pool;

    public FutureHistRadixSort() {
        pool = Executors.newFixedThreadPool(5);
    }

    @Override
    public int[] sort(int[] a) {
        int RADIX = 8;
        int RADICES = 1 << RADIX;
        int mask = (1 << RADIX) - 1;

        try {
            final int[] initData = a;
            Future<int[]>[] hists = new Future[32 / RADIX];
            for (int bits = 0; bits < 32; bits += RADIX) {
                final int closureHack = bits;
                hists[bits / RADIX] = pool.submit(() -> histogram(initData, RADICES, closureHack, mask));
            }

            for (int bits = 0; bits < 32; bits += RADIX) {
                int[] sortedData = new int[a.length];
                // 1st step: Calculate histogram with RADICES entries (RADICES = 1<<RADIX)
                int[] histogram = hists[bits / RADIX].get();

                // 2nd step: Prescan the histogram bucket */
                stepTwo(RADICES, histogram);

                // 3rd step: Rearrange the elements based on prescaned histogram */
                stepThree(a, bits, sortedData, histogram, mask);
                a = sortedData;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("error with pool", e);
        }
        return a;
    }

    private void stepTwo(int RADICES, int[] histogram) {
        int sum = 0;
        for (int i = 0; i < RADICES; ++i) {
            int val = histogram[i];
            histogram[i] = sum;
            sum += val;
        }
    }

    private void stepThree(int[] a, int bits, int[] sortedData, int[] histogram, int mask) {
        for (int i = 0; i < a.length; ++i) {
            sortedData[histogram[(a[i] >> bits) & mask]++] = a[i];
        }
    }

    public int[] histogram(int[] a, int RADICES, int bit, int mask) {
        int[] result = new int[RADICES];
        for (int i = 0; i < a.length; i++) {
            result[(a[i] >> bit) & mask]++;
        }
        return result;
    }

    @Override
    public void close() {
        pool.shutdown();
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
        FutureHistRadixSort radixSort = new FutureHistRadixSort();
        doWork(random, radixSort);
        radixSort.close();
    }

    private static void doWork(Random random, FutureHistRadixSort radixSort) {
        System.out.println("starting");
        for (int i = 0; i < 50; i++) {
            int[] ints = random.ints(10000000, 0, Integer.MAX_VALUE).toArray();
            int[] subject = radixSort.sort(ints);
        }
    }


}
