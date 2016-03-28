package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.MillerRabinPrimalityTest;
import de.hs_augsburg.meixner.primes.PrimeCheck;
import de.hs_augsburg.meixner.utils.profiling.Clock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class RosettaMRPTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void isPrime() throws Exception {
        PrimeCheck primer = new MillerRabinPrimalityTestRosetta();
        assertFalse("1000000000000000001", primer.isPrime(1000000000000000001L));
        logger.info("finished 1");
        assertFalse("1000000000000000002", primer.isPrime(1000000000000000002L));
        logger.info("finished 2");
        assertTrue("1000000000000000003", primer.isPrime(1_000_000_000_000_000_003L));
        logger.info("finished 3");
        assertFalse("1000000000000000004", primer.isPrime(1000000000000000004L));
        logger.info("finished 4");
    }

    @Test
    public void regressionTest() throws Exception {
        final long start = 100000000000000L;
        final long rangeSize = 4000;
        checkAllNumbersInRange(start, start + rangeSize, new MillerRabinPrimalityTest(), new MillerRabinPrimalityTestRosetta());
    }

    private void checkAllNumbersInRange(long first, long last, PrimeCheck a, PrimeCheck b) {
        for (long i = first; i <= last; i++) {
            assertEquals("prime checks should report same result for: " + i, a.isPrime(i), b.isPrime(i));
        }
    }
}