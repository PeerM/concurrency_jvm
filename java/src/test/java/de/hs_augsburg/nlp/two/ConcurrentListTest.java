package de.hs_augsburg.nlp.two;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

public class ConcurrentListTest {
    protected final AtomicInteger putSum = new AtomicInteger(0);
    protected final AtomicInteger takeSum = new AtomicInteger(0);


    private int nPairs = 2;
    private int nTrials = 10;

    public VectorQueue<Integer> vectorQueue;
    public CyclicBarrier barrier;

    @Before
    public void setUp() {
        vectorQueue = new VectorQueue<>();
        barrier = new CyclicBarrier(4);
    }

    @Test
    public void testParallel() {
        try {
            for (int i = 0; i < nPairs; i++) {
                new Thread(new Consumer());
                new Thread(new Producer());
            }
            barrier.await(); // wait for all threads to be ready
            barrier.await(); // wait for all threads to finish
            assertEquals(putSum.get(), takeSum.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static int xorShift(int y) { // cheap random number function
        y ^= (y << 6); y ^= (y >>> 21); y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {

        @Override
        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                barrier.await(); // wait for common start signal
                for (int i = nTrials; i > 0; --i) {
                    vectorQueue.offer(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.getAndAdd(sum);
                barrier.await(); // signal end of computation
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; --i) {
                    sum += vectorQueue.poll();
                }
                takeSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
