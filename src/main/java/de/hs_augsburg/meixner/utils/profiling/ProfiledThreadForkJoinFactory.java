package de.hs_augsburg.meixner.utils.profiling;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class ProfiledThreadForkJoinFactory implements  ForkJoinPool.ForkJoinWorkerThreadFactory {

    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        return new ProfiledForkJoinWorkerThread(pool);
    }

}
