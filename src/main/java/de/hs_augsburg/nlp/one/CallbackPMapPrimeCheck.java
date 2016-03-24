package de.hs_augsburg.nlp.one;

import one.util.streamex.LongStreamEx;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.BiConsumer;

import static java.lang.Math.sqrt;

@SuppressWarnings("Duplicates")
public class CallbackPMapPrimeCheck implements AutoCloseable {
    private ForkJoinPool pool;

    public CallbackPMapPrimeCheck() {
        this.pool = new ForkJoinPool(5);
    }

    public void isPrime(long number, BiConsumer<Long, Boolean> callback) {
        if (number < 2) {
            callback.accept(number, false);
            return;
        }
        if (number == 2) {
            callback.accept(number, true);
            return;
        }
        if (number % 2 == 0) {
            callback.accept(number, false);
            return;
        }
        ForkJoinTask<Boolean> task = pool.submit(() ->
                !LongStreamEx
                        .range(3, ((long) sqrt(number) + 1) / 2, 2)
                        .parallel()
                        .anyMatch(i ->
                                number % i == 0)
        );
        Runnable callBackTask = () -> {
            boolean res = task.join();
            callback.accept(number, res);
        };
        pool.submit(callBackTask);
    }


    @Override
    public void close() throws Exception {
       pool.shutdown();
    }
}
