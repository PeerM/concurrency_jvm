package de.hs_augsburg.nlp.three.radix;


import de.hs_augsburg.nlp.three.histogram.ClojureHelpers;
import one.util.streamex.IntStreamEx;

import java.util.Random;
import java.util.stream.IntStream;


@SuppressWarnings("Duplicates")
public class PHistRadixSort implements ISort {

    private float numSegments;

    public PHistRadixSort() {
        this(Runtime.getRuntime().availableProcessors() * 4);
    }

    public PHistRadixSort(float numSegments) {
        this.numSegments = numSegments;
    }

    @Override
    public int[] sort(int[] a) {
        int RADIX = 8;
        int RADICES = 1 << RADIX;
        int mask = (1 << RADIX) - 1;
        for (int bits = 0; bits < 32; bits += RADIX) {
            int[] sortedData = new int[a.length];
            // 1st step: Calculate histogram with RADICES entries (RADICES = 1<<RADIX)
            int[] histogram = histogram(a, RADICES, bits, mask);

            // 2nd step: Prescan the histogram bucket */
            stepTwo(RADICES, histogram);

            // 3rd step: Rearrange the elements based on prescaned histogram */
            stepThree(a, bits, sortedData, histogram, mask);
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

    private void stepThree(int[] a, int bits, int[] sortedData, int[] histogram, int mask) {
        for (int i = 0; i < a.length; ++i) {
            sortedData[histogram[(a[i] >> bits) & mask]++] = a[i];
        }
    }

    public int[] histogram(int[] a, int RADICES, int bit, int mask) {
        final int absoluteSize = a.length;
        final int segmentSize = (int) Math.ceil(a.length / numSegments);
        int[] ends = new int[absoluteSize / segmentSize];
//        int i = 0;
        int end = segmentSize;
        for (int i = 0; i < ends.length; i++) {
            ends[i] = end;
            end += segmentSize;
        }
        int[] most = IntStream.of(ends)
//                .peek(ints -> {System.out.println(ints[0]+", "+ints[1]);})
                .parallel()
                .boxed()
                .map(segment -> partialHistogram(a, segment - segmentSize, segment, RADICES, bit, mask))
                .reduce(ClojureHelpers::arrayElementBasedAdd).orElseThrow(() -> new RuntimeException("failed to calculate histogram"));
        if (end - segmentSize == absoluteSize) {
            return most;
        }
        int[] rest = partialHistogram(a, end - segmentSize, absoluteSize, RADICES, bit, mask);
        return ClojureHelpers.arrayElementBasedAdd(most, rest);
    }

    public int[] partialHistogram(int[] a, int start, int end, int RADICES, int bit, int mask) {
        int[] result = new int[RADICES];
        for (int i = start; i < end; i++) {
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
