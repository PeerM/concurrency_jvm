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
        return !LongStream
                .iterate(3, i -> i + 2)
                .limit(((long) sqrt(number) + 1) / 2)
                .anyMatch(i -> number % i == 0);
    }
}
