package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.PrimeCheck;

import java.util.stream.LongStream;
import java.util.stream.Stream;

@SuppressWarnings("Duplicates")
public class TaskedPrimeCheck implements PrimeCheck {
    private static final long INTERVAL = 1000;

    private static Task nextTask(Task prev) {
        long nextEnd = prev.end + INTERVAL;
        long endSquared = prev.end * prev.end;
        if (endSquared > prev.number) {
            nextEnd = endSquared;
        }
        return new Task(prev.end, nextEnd, prev.number);
    }

    private static boolean SolveTask(Task t) {
        return LongStream.iterate(t.start, i -> i + 2).limit((t.end - t.start) / 2).anyMatch(i -> t.number % i == 0);
    }

    @Override
    public boolean isPrime(long number) {
        if (number < 2)
            return false;
        if (number == 2)
            return true;
        if (number % 2 == 0)
            return false;
        Stream<Boolean> booleanStream = Stream
                .iterate(new Task(3, 3 + INTERVAL, number), TaskedPrimeCheck::nextTask)
                .map(TaskedPrimeCheck::SolveTask);
        return !booleanStream
                .anyMatch(b -> b);
    }

    private static class Task {
        final long start;
        final long end;
        final boolean shutdown;
        final long number;

        Task() {
            this.start = 0;
            this.end = 0;
            this.number = 0;
            this.shutdown = true;
        }

        Task(long start, long end, long number) {
            this.start = start;
            this.end = end;
            shutdown = false;
            this.number = number;
        }
    }
}
