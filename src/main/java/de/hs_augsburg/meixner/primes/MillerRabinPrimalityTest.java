package de.hs_augsburg.meixner.primes;

import java.math.BigInteger;

public class MillerRabinPrimalityTest implements PrimeCheck {

    public boolean isPrime(long number) {
        BigInteger candidate = BigInteger.valueOf(number);
        return candidate.isProbablePrime(10);
    }

}
