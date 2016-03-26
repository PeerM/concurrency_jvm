package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.MillerRabinPrimalityTest;
import de.hs_augsburg.meixner.primes.PrimeCheck;
import de.hs_augsburg.meixner.primes.SimplePrimeCheck;
import de.hs_augsburg.meixner.utils.profiling.Clock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class TaskedPrimeCheckTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void isPrime() throws Exception {
        PrimeCheck primer = new TaskedPrimeCheck();
        assertFalse("1000000000000000001", primer.isPrime(1000000000000000001L));
        logger.info("finished 1");
        assertFalse("1000000000000000002", primer.isPrime(1000000000000000002L));
        logger.info("finished 2");
        assertTrue("1000000000000000003", primer.isPrime(1_000_000_000_000_000_003L));
        logger.info("finished 3");
        assertFalse("1000000000000000004", primer.isPrime(1000000000000000004L));
        logger.info("finished 4");
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
    public void taskedFasterThanSimple() throws Exception {
        long number = 1000000000000000003L;
        PrimeCheck tasked = new TaskedPrimeCheck();
        PrimeCheck simple = new SimplePrimeCheck();
        doRuntimeComparison(number, "simple", simple, "tasked", tasked);
    }

    @Test
    public void parallelFasterForNonPrime() throws Exception {
        long number = 1000000000000000001L;
        PrimeCheck tasked = new TaskedPrimeCheck();
        PrimeCheck simple = new SimplePrimeCheck();
        doRuntimeComparison(number, "simple", simple, "tasked", tasked);
    }

    @Test
    public void parallelFasterOverall() throws Exception {
        float factor = 0.8f;
        PrimeCheck a = new SimplePrimeCheck();
        PrimeCheck b = new TaskedPrimeCheck();
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

        String aName = "simple";
        String bName = "Tasked";
        logger.info(aName + ": " + aTime + " " + bName + ": " + bTime);
        assertTrue(bName + " should be faster," + aName + ": " + aTime + " " + bName + ": " + bTime, aTime * factor > bTime);
    }

    @Test
    public void profile() throws Exception {
        float factor = 0.8f;
        PrimeCheck b = new TaskedPrimeCheck();
        final long start = 1000000000000000000L;
        final long rangeSize = 50;
        Clock.reset();
        Clock.startRec();
        for (long i = start; i <= start + rangeSize; i++) {
            b.isPrime(i);
        }
        Clock.stopRec();
        long bTime = Clock.elapsed();

        String bName = "Tasked";
        logger.info(bName + ": " + bTime);
    }

    @Test
    public void regressionTest() throws Exception {
        final long start = 1000000000000000000L;
        final long rangeSize = 100;
        checkAllNumbersInRange(start, start + rangeSize, new MillerRabinPrimalityTest(), new TaskedPrimeCheck());
    }

    private void checkAllNumbersInRange(long first, long last, PrimeCheck a, PrimeCheck b) {
        for (long i = first; i <= last; i++) {
            assertEquals("prime checks should report same result for: " + i, a.isPrime(i), b.isPrime(i));
        }
    }
}