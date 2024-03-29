package de.hs_augsburg.nlp.one.prime;

import de.hs_augsburg.meixner.primes.PrimeCheck;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static java.math.BigInteger.ONE;

@SuppressWarnings("Duplicates")
public class MillerRabinPrimalityTestParallel implements PrimeCheck {
// comparable in speed to the non parallel version MillerRabinPrimalityTestRosetta
    static private long nextCanditate(Random r, long upper) {
        return (long) (r.nextDouble() * (upper - 4)) + 2;
    }

    // this code is an adaption of https://rosettacode.org/wiki/Miller%E2%80%93Rabin_primality_test#Python on 28.03.16
    // the original source was licensed under the GNU Free Documentation License
    private boolean isProbablePrime(BigInteger n, int certainty) {

        BigInteger w = n.abs();
        if (w.equals(BigInteger.valueOf(2)))
            return true;
        if (!w.testBit(0) || w.equals(ONE))
            return false;

        BigInteger s = BigInteger.ZERO;
        BigInteger d = n.subtract(ONE);
        while (d.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            d = d.shiftRight(1);
            s = s.add(ONE);
        }
        Random r = new Random();
        final BigInteger finalS = s;
        final BigInteger finalD = d;
        int iterations = numberOfRounds(certainty, n);
        Long[] randomSet = new Long[iterations];
        for (int i = 0; i < randomSet.length; i++) {
            randomSet[i] = nextCanditate(r, n.longValue());
        }
//        LongStream rStream = LongStream.iterate(nextCanditate(r, n.longValue()), p -> nextCanditate(r, n.longValue())).limit(iterations);
        return !Arrays.stream(randomSet).parallel().anyMatch(a -> try_composite(a, finalD, n, finalS));
    }

    // 90% of time is spend in here
    private boolean try_composite(long a, BigInteger d, BigInteger n, BigInteger s) {
        BigInteger aB = BigInteger.valueOf(a);
        if (aB.modPow(d, n).equals(ONE)) {
            return false;
        }
        //1 bis 16
//        for (int i = 0; BigInteger.valueOf(i).compareTo(s) < 0; i++) {
//            // if pow(a, 2**i * d, n) == n-1
//            if (aB.modPow(BigInteger.valueOf(2).pow(i).multiply(d), n).equals(n.subtract(ONE))) {
//                return false;
//            }
//        }
//        return true;
        return !IntStream.range(0,s.intValue())
                .parallel()
                .anyMatch(i -> aB.modPow(BigInteger.valueOf(2).pow(i).multiply(d), n).equals(n.subtract(ONE)));
    }


    // This method is an adaption of the primeToCertainty in The BigInteger class
    private int numberOfRounds(int certainty, BigInteger number) {
        int rounds = 0;
        int n = (Math.min(certainty, Integer.MAX_VALUE - 1) + 1) / 2;

        // The relationship between the certainty and the number of rounds
        // we perform is given in the draft standard ANSI X9.80, "PRIME
        // NUMBER GENERATION, PRIMALITY TESTING, AND PRIMALITY CERTIFICATES".
        int sizeInBits = number.bitLength();
        if (sizeInBits < 100) {
            rounds = 50;
            rounds = n < rounds ? n : rounds;
            return rounds;
        }

        if (sizeInBits < 256) {
            rounds = 27;
        } else if (sizeInBits < 512) {
            rounds = 15;
        } else if (sizeInBits < 768) {
            rounds = 8;
        } else if (sizeInBits < 1024) {
            rounds = 4;
        } else {
            rounds = 2;
        }
        rounds = n < rounds ? n : rounds;
        return rounds;
    }

    @Override
    public boolean isPrime(long number) {
        return isProbablePrime(BigInteger.valueOf(number), 10);
    }

}
