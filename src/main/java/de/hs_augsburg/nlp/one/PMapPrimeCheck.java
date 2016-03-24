package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.PrimeCheck;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.LongSupplier;
import java.util.stream.LongStream;

import static java.lang.Math.sqrt;

@SuppressWarnings("Duplicates")
public class PMapPrimeCheck implements PrimeCheck {
    private ForkJoinPool pool;

    public PMapPrimeCheck() {
        this.pool = new ForkJoinPool(4);
    }

    @Override
    public boolean isPrime(long number) {
        if (number < 2)
            return false;
        if (number == 2)
            return true;
        if (number % 2 == 0)
            return false;
        ForkJoinTask<Boolean> task = pool.submit(() ->
                !LongStream.generate(new CandidateSupplier())
                        .limit(((long) sqrt(number) + 1) / 2)
                        .parallel()
                        .anyMatch(i -> number % i == 0));
        return task.join();
    }

    private class CandidateSupplier implements LongSupplier {
        long i = 1;

        @Override
        public long getAsLong() {
            i += 2;
            return i;
        }
    }

    // is prime test with parallel 60s
    // is prime test without parallel 7s
}
