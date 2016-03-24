package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.MillerRabinPrimalityTest;
import de.hs_augsburg.meixner.primes.PrimeCheck;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PMapPrimeCheckTest {

    @Test
    public void isPrime() throws Exception {
        PrimeCheck primer = new PMapPrimeCheck();
        assertFalse("1000000000000000001", primer.isPrime(1000000000000000001L));
        assertFalse("1000000000000000002", primer.isPrime(1000000000000000002L));
        assertTrue( "1000000000000000003", primer.isPrime(1000000000000000003L));
        assertFalse("1000000000000000004", primer.isPrime(1000000000000000004L));
    }

    @Test
    public void regressionTest() throws Exception {
        final long start = 1000000000000000000L;
        final long rangeSize = 100;
        checkAllNumbersInRange(start, start + rangeSize, new MillerRabinPrimalityTest(), new PMapPrimeCheck());
    }

    private void checkAllNumbersInRange(long first, long last, PrimeCheck a, PrimeCheck b) {
        for (long i = first; i <= last; i++) {
            assertEquals("prime checks should report same result for: " + i, a.isPrime(i), b.isPrime(i));
        }
    }
}