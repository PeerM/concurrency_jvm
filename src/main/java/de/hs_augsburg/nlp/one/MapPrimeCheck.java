package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.PrimeCheck;

import java.util.function.LongSupplier;
import java.util.stream.LongStream;

import static java.lang.Math.sqrt;

@SuppressWarnings("Duplicates")
public class MapPrimeCheck implements PrimeCheck {
    @Override
    public boolean isPrime(long number) {
        if (number < 2)
            return false;
        if (number == 2)
            return true;
        if (number % 2 == 0)
            return false;
        return !LongStream.generate(new CandidateSupplier()).limit(((long) sqrt(number) + 1) / 2).anyMatch(i -> number % i == 0);
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
