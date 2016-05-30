package de.hs_augsburg.meixner.utils.profiling;

import java.util.concurrent.ThreadFactory;

public class ProfiledThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        return new ProfiledThread(r);
    }
}
