package de.hs_augsburg.nlp.one;

public class PrimeChecker {
    public static boolean doesRangeContainDivider(long start, long end, long number) {
        for (long i = start; i < end; i += 2) {
            if (number % i == 0)
                return true;
        }
        return false;
    }
}
