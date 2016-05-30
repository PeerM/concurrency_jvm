package de.hs_augsburg.nlp.three.radix;


import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@SuppressWarnings("Duplicates")
public class ForkHistRadixSort implements ISort {

    private ForkJoinPool pool;

    public ForkHistRadixSort() {
        pool = new ForkJoinPool(5);
    }

    @Override
    public int[] sort(int[] a) {
        int RADIX = 8;
        int RADICES = 1 << RADIX;
        int mask = (1 << RADIX) - 1;

        int[] localA = a;
        ForkJoinTask<int[]>[] hists = new ForkJoinTask[32];
        for (int bits = RADIX; bits < 32; bits += RADIX) {
            hists[bits] = pool.submit(new ForkHist(a, RADICES, bits, mask));
        }
        ForkJoinTask<int[]> firstTask = hists[0] = new ForkHist(a, RADICES, 0, mask);
        firstTask.invoke();

        for (int bits = 0; bits < 32; bits += RADIX) {
            int[] sortedData = new int[localA.length];
            // 1st step: Calculate histogram with RADICES entries (RADICES = 1<<RADIX)
            int[] histogram = hists[bits].join();

            // 2nd step: Prescan the histogram bucket */
            stepTwo(RADICES, histogram);

            // 3rd step: Rearrange the elements based on prescaned histogram */
            stepThree(localA, bits, sortedData, histogram, mask);
            localA = sortedData;
        }
        return localA;

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

    private static class ForkHist extends ForkJoinTask<int[]> {

        final int[] a;
        final int RADICES;
        final int bit;
        final int mask;
        int[] result;

        public ForkHist(int[] a, int RADICES, int bit, int mask) {
            this.a = a;
            this.RADICES = RADICES;
            this.bit = bit;
            this.mask = mask;
        }

        @Override
        public int[] getRawResult() {
            return result;
        }

        @Override
        protected void setRawResult(int[] value) {
            this.result = value;
        }

        @Override
        protected boolean exec() {
            int[] result = new int[RADICES];
            for (int i = 0; i < a.length; i++) {
                result[(a[i] >> bit) & mask]++;
            }
            this.result = result;
            return true;
        }
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
        ForkHistRadixSort radixSort = new ForkHistRadixSort();
        doWork(random, radixSort);
        radixSort.close();
    }

    private static void doWork(Random random, ForkHistRadixSort radixSort) {
        System.out.println("starting");
        for (int i = 0; i < 50; i++) {
            int[] ints = random.ints(10000000, 0, Integer.MAX_VALUE).toArray();
            int[] subject = radixSort.sort(ints);
        }
    }


}
