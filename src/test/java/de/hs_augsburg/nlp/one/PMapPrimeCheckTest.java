package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.MillerRabinPrimalityTest;
import de.hs_augsburg.meixner.primes.PrimeCheck;
import de.hs_augsburg.meixner.primes.SimplePrimeCheck;
import de.hs_augsburg.meixner.utils.profiling.Clock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class PMapPrimeCheckTest {

    @Test
    public void isPrime() throws Exception {
        PrimeCheck primer = new PMapPrimeCheck();
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

    @Test
    public void parallelFasterForNonePrime() throws Exception {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        PrimeCheck pmap = new PMapPrimeCheck();
        PrimeCheck map = new MapPrimeCheck();
        long number = 1000000000000000003L;
        Clock.startRec();
        map.isPrime(number);
        Clock.stopRec();
        long mapTime = Clock.elapsed();
        Clock.reset();
        Clock.startRec();
        pmap.isPrime(number);
        Clock.stopRec();
        long pMapTime = Clock.elapsed();
        Clock.reset();

        logger.info("map: " + mapTime + " pmap: " + pMapTime);
        assertTrue("pmap should be faster for nonPrimes,map: " + mapTime + " pmap: " + pMapTime, mapTime > pMapTime);
    }

    @Test
    public void parallelFasterThanSimple() throws Exception {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        PrimeCheck pmap = new PMapPrimeCheck();
        PrimeCheck simple = new SimplePrimeCheck();
        long number = 1000000000000000003L;
        Clock.startRec();
        simple.isPrime(number);
        Clock.stopRec();
        long simpleTime = Clock.elapsed();
        Clock.reset();
        Clock.startRec();
        pmap.isPrime(number);
        Clock.stopRec();
        long pMapTime = Clock.elapsed();
        Clock.reset();

        logger.info("simple: " + simpleTime + " pmap: " + pMapTime);
        assertTrue("pmap should be faster for nonPrimes,map: " + simpleTime + " pmap: " + pMapTime, simpleTime > pMapTime);
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