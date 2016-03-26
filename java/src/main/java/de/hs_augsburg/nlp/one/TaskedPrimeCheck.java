package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.primes.PrimeCheck;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@SuppressWarnings("Duplicates")
public class TaskedPrimeCheck implements PrimeCheck, AutoCloseable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ForkJoinPool pool;

    public TaskedPrimeCheck() {
        this.pool = new ForkJoinPool();
    }

    private static long interval(long number) {
        //return 10000;
        // this effectively controls the number of tasks created
        // a number less than 50000L makes the tasks run synchronous
        long q = (Math.round(Math.sqrt(number) + 1) / 1000000L);
        // tasks for less than 500 potential dividers make no sense
        return Math.max(q, 500);

    }

    private static Task nextTask(Task prev, Long interval) {
        long nextEnd = prev.end + interval;
        long endSquared = nextEnd * nextEnd;
        if (endSquared > prev.number) {
            nextEnd = Math.round(Math.sqrt(prev.number) + 1);
        }
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
                .takeWhile(t -> (t.start * t.start <= t.number))
                //.peek((task -> logger.info(task.toString())))
                ;
        ForkJoinTask<Boolean> forkJoinTask = pool.submit(() ->
                !tasks
                        .parallel()
                        .map(TaskedPrimeCheck::dividerInTask)
                        //.peek(b -> logger.info("stream got " + b))
                        .anyMatch(b -> b));
        return forkJoinTask.join();
    }

    @Override
    public void close() throws Exception {
        pool.shutdown();
    }

    private static class Task {
        final long start;
        final long end;
        final long number;

        Task(long start, long end, long number) {
            this.start = start;
            this.end = end;
            this.number = number;
        }

        @Override
        public String toString() {
            return "start: " + start + ", end: " + end;
        }
    }
}
