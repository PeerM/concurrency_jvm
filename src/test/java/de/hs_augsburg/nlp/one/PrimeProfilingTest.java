package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.MillerRabinPrimalityTest;
import de.hs_augsburg.meixner.primes.PrimeCheck;
import de.hs_augsburg.meixner.primes.SimplePrimeCheck;
import de.hs_augsburg.nlp.one.prime.*;
import de.hs_augsburg.nlp.util.TimeIt;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
@Ignore
public class PrimeProfilingTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void profileMR() throws Exception {
        PrimeCheck checker = new MillerRabinPrimalityTest();
        profile(checker, "MilerRabin");
    }

    @Test
    public void profileMRRosetta() throws Exception {
        PrimeCheck checker = new MillerRabinPrimalityTestRosetta();
        profile(checker, "MilerRabin");
    }

    @Test
    public void profileMRParallel() throws Exception {
        PrimeCheck checker = new MillerRabinPrimalityTestParallel();
        profile(checker, "MilerRabin Parallel");
    }

    @Test
    public void profileSimple() throws Exception {
        PrimeCheck checker = new SimplePrimeCheck();
        profile(checker, "Simple");
    }

    @Test
    public void profileParallelNaive() throws Exception {
        PrimeCheck checker = new PMapPrimeCheck();
        profile(checker, "ParallelJavaMap");
    }

    @Test
    public void profileMap() {
        PrimeCheck checker = new MapPrimeCheck();
        profile(checker, "MapPrimeCheck");

    }

    @Test
    public void profileTasked() {
        PrimeCheck checker = new TaskedPrimeCheck();
        profile(checker, "Tasked Prime");

    }

    private void profile(PrimeCheck checker, String name) {
        TimeIt.TimeData<List<Boolean>> timeData = TimeIt.timeIt(() -> LongStream.range(1000000000000000000L, 1000000000000000006L).boxed().map(checker::isPrime).collect(Collectors.toList()));
        logger.info(String.format("%s: e:%d, ecpu:%d", name, timeData.elapsed, timeData.elapsedCpu));
    }
}