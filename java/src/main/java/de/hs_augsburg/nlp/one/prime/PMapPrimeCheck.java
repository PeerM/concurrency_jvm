package de.hs_augsburg.nlp.one.prime;

import de.hs_augsburg.meixner.primes.PrimeCheck;
import one.util.streamex.LongStreamEx;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static java.lang.Math.sqrt;

@SuppressWarnings("Duplicates")
public class PMapPrimeCheck implements PrimeCheck {
    private ForkJoinPool pool;

    public PMapPrimeCheck() {
        this.pool = new ForkJoinPool(5);
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
                !LongStreamEx
                        .range(3, ((long) sqrt(number) + 1) / 2, 2)
                        .parallel()
                        .anyMatch(i ->
                                number % i == 0)
        );
        Boolean result = task.join();
        pool.shutdown();
        return result;
    }
}
