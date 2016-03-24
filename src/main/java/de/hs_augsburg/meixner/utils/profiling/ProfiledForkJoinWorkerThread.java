package de.hs_augsburg.meixner.utils.profiling;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class ProfiledForkJoinWorkerThread extends ForkJoinWorkerThread {
    public ProfiledForkJoinWorkerThread(ForkJoinPool pool) {
        super(pool);
    }

    @Override
    public void run() {
        Clock.getDefaultInstance().startRec(this);
        super.run();
        Clock.getDefaultInstance().stopRec(this);
    }
}
