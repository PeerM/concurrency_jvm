package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.MillerRabinPrimalityTest;
import de.hs_augsburg.meixner.primes.PrimeCheck;
import de.hs_augsburg.meixner.primes.SimplePrimeCheck;
import de.hs_augsburg.meixner.utils.profiling.Clock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class PMapPrimeCheckTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void isPrimePMap() throws Exception {
        PrimeCheck primer = new PMapPrimeCheck();
        assertFalse("1000000000000000001", primer.isPrime(1000000000000000001L));
        assertFalse("1000000000000000002", primer.isPrime(1000000000000000002L));
        assertTrue("1000000000000000003", primer.isPrime(1000000000000000003L));
        assertFalse("1000000000000000004", primer.isPrime(1000000000000000004L));
    }

    @Test
    public void isPrimeMap() throws Exception {
        PrimeCheck primer = new MapPrimeCheck();
        assertFalse("1000000000000000001", primer.isPrime(1000000000000000001L));
        assertFalse("1000000000000000002", primer.isPrime(1000000000000000002L));
        assertTrue("1000000000000000003", primer.isPrime(1000000000000000003L));
        assertFalse("1000000000000000004", primer.isPrime(1000000000000000004L));
    }

//    @Test
//    public void parallelFaster() throws Exception {
//        PrimeCheck pmap = new PMapPrimeCheck();
//        PrimeCheck map = new MapPrimeCheck();
//        long start = 1000000000000000000L;
//        long range = 100;
//        long i;
//        for (i = start; i < start + range; i++) {
//
//        }
//        assertFalse("1000000000000000001", primer.isPrime(1000000000000000001L));
//        assertFalse("1000000000000000002", primer.isPrime(1000000000000000002L));
//        assertTrue("1000000000000000003", primer.isPrime(1000000000000000003L));
//        assertFalse("1000000000000000004", primer.isPrime(1000000000000000004L));
//    }

    public void doRuntimeComparison(long number, String aName, PrimeCheck a, String bName, PrimeCheck b) {
        float factor = 0.8f;
        Clock.startRec();
        a.isPrime(number);
        Clock.stopRec();
        long aTime = Clock.elapsed();
        Clock.reset();
        Clock.startRec();
        b.isPrime(number);
        Clock.stopRec();
        long bTime = Clock.elapsed();
        Clock.reset();

        logger.info(aName + ": " + aTime + " " + bName + ": " + bTime);
        assertTrue(bName + " should be faster," + aName + ": " + aTime + " " + bName + ": " + bTime, aTime * factor > bTime);
    }

    @Test
    public void parallelFasterForPrime() throws Exception {
        PrimeCheck pmap = new PMapPrimeCheck();
        PrimeCheck map = new MapPrimeCheck();
        long number = 1000000000000000003L;
        doRuntimeComparison(number, "map", map, "pmap", pmap);
    }

    @Test
    public void parallelFasterForNonPrime() throws Exception {
        PrimeCheck pmap = new PMapPrimeCheck();
        PrimeCheck map = new MapPrimeCheck();
        long number = 1000000000000000001L;
        doRuntimeComparison(number, "map", map, "pmap", pmap);
    }

    @Test
    public void parallelFasterThanSimple() throws Exception {
        PrimeCheck pmap = new PMapPrimeCheck();
        PrimeCheck simple = new SimplePrimeCheck();
        long number = 1000000000000000003L;
        doRuntimeComparison(number, "simple", simple, "pmap", pmap);
    }

    @Test
    public void parallelFasterThanSimpleWithNonPrime() throws Exception {
        PrimeCheck pmap = new PMapPrimeCheck();
        PrimeCheck simple = new SimplePrimeCheck();
        long number = 1000000000000000001L;
        doRuntimeComparison(number, "simple", simple, "pmap", pmap);
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