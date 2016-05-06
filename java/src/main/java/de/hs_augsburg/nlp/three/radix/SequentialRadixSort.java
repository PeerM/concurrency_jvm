package de.hs_augsburg.nlp.three.radix;


import java.util.Random;

public class SequentialRadixSort implements ISort {
    @Override
    public int[] sort(int[] a) {
        int RADIX = 8;
        int RADICES = 1 << RADIX;
        for (int bits = 0; bits < 32; bits += RADIX) {
            int[] sortedData = new int[a.length];
            // 1st step: Calculate histogram with RADICES entries (RADICES = 1<<RADIX)
            int[] histogram = histogram(a, RADICES, bits);

            // 2nd step: Prescan the histogram bucket */
            stepTwo(RADICES, histogram);

            // 3rd step: Rearrange the elements based on prescaned histogram */
            stepThree(a, RADICES, bits, sortedData, histogram);
            a = sortedData;
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

    private void stepThree(int[] a, int RADICES, int bits, int[] sortedData, int[] histogram) {
        for (int i = 0; i < a.length; ++i) {
            sortedData[histogram[(a[i] >> bits) % RADICES]++] = a[i];
        }
    }

    public static int[] histogram(int[] a, int RADICES, int bit) {
        int[] result = new int[RADICES];
        for (int i = 0; i < a.length; i++) {
            result[(a[i] >> bit) % RADICES]++;
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
        SequentialRadixSort radixSort = new SequentialRadixSort();
        doWork(random, radixSort);
    }

    private static void doWork(Random random, SequentialRadixSort radixSort) {
        System.out.println("starting");
        for (int i = 0; i < 50; i++) {
            int[] ints = random.ints(10000000, 0, Integer.MAX_VALUE).toArray();
            int[] subject = radixSort.sort(ints);
        }
    }
}
