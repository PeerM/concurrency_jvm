package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.PrimeCheck;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("Duplicates")
public class TaskedPrimeCheck implements PrimeCheck {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static long interval(long number) {
        return 500000;
        //return number / 2000;
    }

    private static Task nextTask(Task prev, Long interval) {
        long nextEnd = prev.end + interval;
        long endSquared = prev.end * prev.end;
        return new Task(prev.end, nextEnd, prev.number);
    }

    private static boolean dividerInTask(Task t) {
        for (long i = t.start; i < t.end; i += 2) {
            if (t.number % i == 0)
                return true;
        }
        return false;
    }

    @Override
    public boolean isPrime(long number) {
        if (number < 2)
            return false;
        if (number == 2)
            return true;
        if (number % 2 == 0)
            return false;
        long interval = interval(number);
        StreamEx<Task> tasks = StreamEx
                .iterate(new Task(3, 3 + interval, number), t -> nextTask(t, interval))
                .takeWhile(t -> (t.start * t.start <= t.number));
        StreamEx<Boolean> solutions = tasks
                .map(TaskedPrimeCheck::dividerInTask)
                //.peek(b -> logger.info("stream got " + b))
                ;
        return !solutions
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
