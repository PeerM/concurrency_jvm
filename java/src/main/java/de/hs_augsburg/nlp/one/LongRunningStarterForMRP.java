package de.hs_augsburg.nlp.one;


import de.hs_augsburg.meixner.primes.PrimeCheck;

public class LongRunningStarterForMRP {
    public static void main(String[] args) {
        longRunning();
    }

    private static void longRunning() {
        final long start = 100000000000000L;
        final long rangeSize = 4000000;
        PrimeCheck checker = new MillerRabinPrimalityTestRosetta();
        for (long i = start; i <= start + rangeSize; i++) {
            checker.isPrime(i);
        }
    }
}
