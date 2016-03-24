package de.hs_augsburg.meixner.primes;

public class SimplePrimeCheck implements PrimeCheck {

    public boolean isPrime(long number) {
        if (number < 2)
            return false;
        if (number==2)
            return true;
        if (number % 2 == 0)
            return false;
        for (long i=3; i*i <= number; i+=2) {
            if (number%i == 0) 
                return false;
        }
        return true;
    }
}
