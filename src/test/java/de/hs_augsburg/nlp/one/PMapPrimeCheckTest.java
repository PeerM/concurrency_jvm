package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.MillerRabinPrimalityTest;
import de.hs_augsburg.meixner.primes.PrimeCheck;
import de.hs_augsburg.meixner.primes.SimplePrimeCheck;
import de.hs_augsburg.meixner.utils.profiling.Clock;
import de.hs_augsburg.nlp.one.prime.CallbackPMapPrimeCheck;
import de.hs_augsburg.nlp.one.prime.MapPrimeCheck;
import de.hs_augsburg.nlp.one.prime.PMapPrimeCheck;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
@Ignore
public class PMapPrimeCheckTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void isPrimePMap() throws Exception {
        PrimeCheck primer = new PMapPrimeCheck();
        assertFalse("1000000000000000001", primer.isPrime(1000000000000000001L));
        assertFalse("1000000000000000002", primer.isPrime(1000000000000000002L));
        assertTrue("1000000000000000003", primer.isPrime(1000000000000000003L));
        assertFalse("1000000000000000004", primer.isPrime(1000000000000000004L));
        // yes this test and other test are leaking threads but I am to lazy to call close
    }

    @Test
    public void isPrimeMap() throws Exception {
        PrimeCheck primer = new MapPrimeCheck();
        assertFalse("1000000000000000001", primer.isPrime(1000000000000000001L));
        assertFalse("1000000000000000002", primer.isPrime(1000000000000000002L));
        assertTrue("1000000000000000003", primer.isPrime(1000000000000000003L));
        assertFalse("1000000000000000004", primer.isPrime(1000000000000000004L));
    }

    @Test
    public void isPrimeCallbackMap() throws Exception {

        try (CallbackPMapPrimeCheck primer = new CallbackPMapPrimeCheck();){
            AtomicInteger numberOfCallbacks = new AtomicInteger(0);
            primer.isPrime(1000000000000000001L, (n, isP) -> {
                assertFalse("1000000000000000001", isP);
                numberOfCallbacks.getAndIncrement();
                logger.info("1000000000000000001 finished");
            });
            primer.isPrime(1000000000000000002L, (n, isP) -> {
                assertFalse("1000000000000000002", isP);
                numberOfCallbacks.getAndIncrement();
                logger.info("1000000000000000002 finished");
            });
            primer.isPrime(1000000000000000003L, (n, isP) -> {
                assertTrue("1000000000000000003", isP);
                numberOfCallbacks.getAndIncrement();
                logger.info("1000000000000000003 finished");
            });
            primer.isPrime(1000000000000000004L, (n, isP) -> {
                assertFalse("1000000000000000004", isP);
                numberOfCallbacks.getAndIncrement();
                logger.info("1000000000000000004 finished");
            });
            Thread.sleep(6000);

            assertEquals("Not all 4 Callbacks called", 4, numberOfCallbacks.get());
        }
    }


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

    @Ignore
    @Test
    public void parallelFasterForMost() throws Exception {
        PrimeCheck pmap = new PMapPrimeCheck();
        PrimeCheck map = new MapPrimeCheck();
        final long start = 1000000000000000000L;
        final long rangeSize = 50;
        for (long i = start; i <= start + rangeSize; i++) {
            doRuntimeComparison(i, "map", map, "pmap", pmap);
        }
    }

    @Test
    public void parallelFasterOverall() throws Exception {
        float factor = 0.8f;
        PrimeCheck a = new MapPrimeCheck();
        PrimeCheck b = new PMapPrimeCheck();
        final long start = 1000000000000000000L;
        final long rangeSize = 50;
        Clock.startRec();
        for (long i = start; i <= start + rangeSize; i++) {
            a.isPrime(i);
        }
        Clock.stopRec();
        long aTime = Clock.elapsed();

        Clock.reset();
        Clock.startRec();
        for (long i = start; i <= start + rangeSize; i++) {
            b.isPrime(i);
        }
        Clock.stopRec();
        long bTime = Clock.elapsed();

        String aName = "map";
        String bName = "pmap";
        logger.info(aName + ": " + aTime + " " + bName + ": " + bTime);
        assertTrue(bName + " should be faster," + aName + ": " + aTime + " " + bName + ": " + bTime, aTime * factor > bTime);
    }

    @Test
    public void parallelFasterThanSimple() throws Exception {
        PrimeCheck pmap = new PMapPrimeCheck();
        PrimeCheck simple = new SimplePrimeCheck();
        long number = 1000000000000000003L;
        doRuntimeComparison(number, "simple", simple, "pmap", pmap);
    }

    @Ignore
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