package de.hs_augsburg.nlp.three.radix;


public class SequentialRadixSort implements ISort {
    @Override
    public int[] sort(int[] a) {
        int RADIX =   2;
        int RADICES = 1 << RADIX;
        for (int bits = 0; bits < 32; bits += RADIX) {
            int[] sortedData = new int[a.length];
            // 1st step: Calculate histogram with RADICES entries (RADICES = 1<<RADIX)
            int[] histogram = histogram(a, RADICES, bits);


            // 2nd step: Prescan the histogram bucket */
            int sum = 0;
            for (int i = 0; i < RADICES; ++i) {
                int val = histogram[i];
                histogram[i] = sum;
                sum += val;
            }

            // 3rd step: Rearrange the elements based on prescaned histogram */
            for (int i = 0; i < a.length; ++i) {
                sortedData[histogram[(a[i] >> bits) % RADICES]++] = a[i];
            }
            a = sortedData;
        }
        return a;
    }

    public static int[] histogram(int[] a, int RADICES, int bit) {
        int[] result = new int[RADICES];
        for (int i = 0; i < a.length; i++) {
            result[(a[i] >> bit) % RADICES]++;
        }
        return result;
    }
}
